// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Context, DCOP, Value, Variable}
import akka.actor.{Actor, ActorRef}
import org.adopt.problem.ToyExample._

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
  variable.domain.head
  // lower bound
  private var lb = Map[(Value,Variable),Double]()
  // upper bound
  private var ub = Map[(Value,Variable),Double]()

  /**
    * Resets bounds
    */
  def resetBound() : Unit ={
    variable.domain.foreach{ v: Value =>
      children.foreach{ child : Variable =>
        lb += ((v, child) -> 0.0)
        ub += ((v, child) -> Double.MaxValue)
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
    localCost(value) + children.toSeq.map( v => lb(value,v) ).sum
  }

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
      if (variable==x3) {
        ctxt.fix(Map(x1-> f, x2 -> f, x3 -> t, x4 -> t))
        if (debug) println("x3=t localcost: "+localCost(f))

      }
      solverAgent ! Assign(variable.domain.head)

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
