// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Context, DCOP, Value, Variable}
import akka.actor.{Actor, ActorRef}

/**
  * SolverAgent which starts and stops the computation of an allocation
  * @param variable which should be valuated
  * @param pb DCOP instance
  * */

class VariableAgent(variable : Variable, pb : DCOP) extends Actor {
  var debug = true
  // The actor which triggers the resolution
  private var solverAgent : ActorRef= context.parent
  // White page variable/actor
  private var directory = new Directory()
  // parent variable in the DFS
  private var parent : Option[Variable] = None
  // Children variables in the DFS
  private var children = Set[Variable]()
  // Agent's view of the assignment of higher neigbors
  private val ctxt : Context = new Context(pb)
  // lower bound for the different couples (value,child)
  private var lb = Map[(Value,Variable),Double]()
  // upper bound for the different couples (value,child)
  private var ub = Map[(Value,Variable),Double]()
  // The backtrack threshold  for the different couples (value,child)
  private var threshold = Map[(Value,Variable),Double]()

  /**
    * Returns true if the agent is a leaf agent
    */
  def isLeafAgent: Boolean = children.isEmpty

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
        threshold += ((v, child) -> 0.0)
      }
    }
  }

  /**
    * Returns the local cost for a specific value
    */
  def localCost(value: Value) : Double = {
    var cost = 0.0
    ctxt.valuation.keys.filter(_ == variable).foreach { otherVariable =>
      pb.constraints.foreach { c =>
        if (c.isOver(variable) && c.isOver(otherVariable)) {
          val cc = c.cost(variable, ctxt.valuation(variable), otherVariable, ctxt.valuation(otherVariable))
          if (debug) println(s"Relevant constraint $c = $cc")
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
    localCost(value) + (if (! isLeafAgent) children.toSeq.map( v => lb(value,v) ).sum else 0.0)
  }

  /**
    * Returns the upper bound for the subtree rooted at the variable
    * when the variable chooses value
    */
  def UB(value: Value) : Double = {
    localCost(value) + (if (! isLeafAgent) children.toSeq.map( v => ub(value,v) ).sum else 0.0)
  }

  /**
    * Returns a lower bound for the subtree rooted at the variable
    */
  def LB() : Double = variable.domain.map(v => LB(v)).min

  /**
    * Returns a upper bound for the subtree rooted at the variable
    */
  def UB() : Double = variable.domain.map(v => UB(v)).min

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
      // Initiate lb/up
      resetBound()
      ctxt.fix(Map())
      solverAgent ! Assign(variable.domain.head)

    // When the parent agent sets the threshold value
    case Threshold(t) =>
      //TODO threshold = t

    // When debugging mode is triggered
    case Trace =>
      debug = true
      directory.allActors().foreach(_ ! Trace)

    // When the termination of the variable agent is triggered
    case Stop =>
      context.stop(self)

    // Unexpected message
    case msg@_ =>
      println(s"WARNING: VariableAgent $variable receives a message which was not expected: " + msg)
  }
}
