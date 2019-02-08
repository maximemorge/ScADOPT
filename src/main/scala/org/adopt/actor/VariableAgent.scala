// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Context, DCOP, Value, Variable}
import org.adopt.util.MathUtils.MathUtils

import akka.actor.{Actor, ActorRef}

/**
  * SolverAgent which starts and stops the computation of an allocation
  * @param variable which should be valuated
  * @param pb DCOP instance
  * */

class VariableAgent(variable : Variable, pb : DCOP) extends Actor {
  var debug = true
  var trace = true
  // The actor which triggers the resolution
  private var solverAgent : ActorRef= context.parent
  // White page variable/actor
  private var directory = new Directory()
  // parent variable in the DFS
  private var parent : Option[Variable] = None
  // Children variables in the DFS
  private var children = Set[Variable]()
  // Agent's view of the assignment of higher neighbors
  private var currentContext : Context = new Context(pb)
  // lower bound for the different couples (value,child)
  private var lb = Map[(Value,Variable),Double]()
  // upper bound for the different couples (value,child)
  private var ub = Map[(Value,Variable),Double]()
  // The backtrack threshold for the different couples (value,child)
  private var t = Map[(Value,Variable),Double]()
  private var threshold : Double = 0.0
  // Current value of the variable
  private var di : Value = variable.domain.head
  // True if the agent has received a terminate message from the parent
  private var terminated : Boolean = false

  /**
    * Returns true if the agent is a leaf agent
    */
  def isLeafAgent: Boolean = children.isEmpty

  /**
    * Returns true if the agent is a root agent
    */
  def isRootAgent: Boolean = parent.isEmpty

  /**
    * Returns the lower level neighbors
    */
  def neighbors() : Set[ActorRef] = children.map(v => directory.adr(v))

  /**
    * Returns the value which minimizes a bound
    */
  def minimize(bound : Value=> Double) : Value = {
    var dj = variable.domain.head
    var min = Double.MaxValue
    variable.domain.foreach { d =>
      if (bound(d) < min) {
        dj = d
        min = bound(d)
      }
    }
    dj
  }

  /**
    * Resets bounds
    */
  def resetBound() : Unit ={
    //If the agent is not a leaf but has not yet received any COST messages from its children,
    // UB is equal to maximum value Inf and LB is equal to the minimum local cost δ(d) over all value choices d ∈ Di .
    variable.domain.foreach{ v: Value =>
      children.foreach{ child : Variable =>
        lb += ((v, child) -> 0.0)
        ub += ((v, child) -> Double.MaxValue)
        t += ((v, child) -> 0.0)
      }
    }
  }

  /**
    * Returns the local cost for a specific value
    */
  def ∂(value: Value) : Double = {
    var cost = 0.0
    currentContext.valuation.keys.filter(_ == variable).foreach { otherVariable =>
      pb.constraints.foreach { c =>
        if (c.isOver(variable) && c.isOver(otherVariable)) {
          val cc = c.cost(variable, currentContext.valuation(variable), otherVariable, currentContext.valuation(otherVariable))
          cost += cc
        }
      }
    }
    cost
  }

  /**
    * Returns the lower bound for the subtree rooted at the variable
    * when the variable chooses value
    */
  def LB(value: Value) : Double = {
    ∂(value) + (if (! isLeafAgent) children.toSeq.map(v => lb(value,v) ).sum else 0.0)
  }

  /**
    * Returns the upper bound for the subtree rooted at the variable
    * when the variable chooses value
    */
  def UB(value: Value) : Double = {
    ∂(value) + (if (! isLeafAgent) children.toSeq.map(v => ub(value,v) ).sum else 0.0)
  }

  /**
    * Returns a lower bound for the subtree rooted at the variable
    */
  def LB : Double = variable.domain.map(v => LB(v)).min

  /**
    * Returns a upper bound for the subtree rooted at the variable
    */
  def UB : Double = variable.domain.map(v => UB(v)).min

  // A leaf agent has no subtree so δ(d) = LB(d) = UB(d) for all value choices d and thus,
  // LB is always equal to UB at a leaf.

  /**
    * Message handling
    */
  override def receive: Receive = {
    // When the variable agent is initiated
    case Init(d, p, c) =>
      solverAgent = sender
      directory = d
      parent = p
      children = c
      initialize()

    // When the parent terminate
    case Terminate(ctxt) =>
      terminated = true
      currentContext = ctxt
      backtrack()

    // When the parent agent sets the threshold value
    case Threshold(thresholdValue,ctxt) =>
      if (ctxt.isCompatible(currentContext)){
        threshold = thresholdValue
        maintainThresholdInvariant()
        backtrack()
      }

    // When debugging mode is triggered
    case Trace =>
      trace = true

    // Unexpected message
    case msg@_ =>
      println(s"WARNING: VariableAgent $variable receives a message from ${directory.variables(sender)} which was not expected: " + msg)
  }


  /**
    * Initialize procedure
    */
  def initialize() : Unit = {
    if (debug) println(s"Agent $variable initializes")
    threshold = 0.0
    currentContext.fix(Map())
    resetBound()// initiate lb/up
    di = minimize(LB)// di ← d that minimizes LB(d)
    //solverAgent ! Assign(di)
    backtrack()
  }

  /**
    * Backtrack procedure
    */
  def backtrack() : Unit = {
    if (debug) println(s"Agent $variable backtracks")
    if (threshold ~= UB) {
      di = minimize(UB)// di ← d that minimizes UB(d)
    }else if(LB(di) > threshold){
      di = minimize(LB)// di ← d that minimizes LB(d)
    }
    // Sends value to each lower priority neighbor;
    neighbors().foreach{ neighbor =>
      if (trace) println(s"$variable -> ${directory.variables(neighbor)} : Assign($di)")
      neighbor ! Assign(di)
    }
    maintainAllocationInvariant()
    if (threshold ~= UB){
      if (terminated || isRootAgent) {
        currentContext.fix(variable,di)
        neighbors().foreach{ neighbor =>
          if (trace) println(s"$variable -> ${directory.variables(neighbor)} : Terminate($currentContext)")
          neighbor ! Terminate(currentContext)
        }
        solverAgent ! Assign(di)
        context.stop(self)
      }
    }
    // Sends Cost to parent
    currentContext.fix(variable,di)
    if (parent.isDefined){
      if (trace) println(s"$variable -> ${parent.get} : Cost($LB, $UB, $currentContext) ")
      directory.adr(parent.get) ! Cost(LB, UB, currentContext)
    }
  }

  /**
    * Maintains allocation invariant
    */
  def maintainAllocationInvariant() : Unit = {
    if (debug) println(s"Agent $variable maintains allocation invariant")
    //we assume thresholdInvariant is satisfied
    maintainThresholdInvariant()
    while (threshold > ∂(di) + children.toSeq.map(xl => t(di, xl)).sum) {
      //choose xl ∈Children where ub(di,xl)>t(di,xl);
      val xl = children.find(xl => ub(di, xl) > t(di, xl))
      if (xl.isDefined) t += ((di, xl.get) -> (t(di, xl.get) + 1.00))
    }
    while (threshold < ∂(di) + children.toSeq.map(xl => t(di, xl)).sum) {
      //choose xl ∈Children where t(di,xl)>lb(di,xl);
      val xl = children.find(xl => t(di, xl) > lb(di, xl))
      if (xl.isDefined) t += ((di, xl.get) -> (t(di, xl.get) - 1.00))
    }
    currentContext.fix(variable,di)
    children.foreach{ xl =>
      if (trace) println(s"$variable -> $xl : Threshold(${t(di ,xl)}, $currentContext)")
      directory.adr(xl) ! Threshold(t(di ,xl), currentContext)
    }
  }

  /**
    * Maintains threshold invariant
    */
  def maintainThresholdInvariant() : Unit = {
    if (debug) println(s"Agent $variable maintains threshold invariant")
    if (threshold < LB) threshold = LB
    if (threshold > UB) threshold = UB
  }

}
