// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{DCOP, Variable, Value, Context}

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
          val index1 = c.variable1.index(ctxt.valuation(c.variable1))
          val index2 = c.variable2.index(ctxt.valuation(c.variable2))
          cost += c.cost(index1)(index2)
        }
      }
    }
    cost
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
