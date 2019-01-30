// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Assignment, DCOP, Variable}
import org.adopt.dfs.DFS

import akka.actor.{Actor, ActorRef, Props}

/**
  * SolverAgent which starts and stops the computation of an allocation
  * @param variable which should be valuated
  * @param pb DCOP instance
  * */

class VariableAgent(variable : Variable, pb : DCOP) extends Actor {
  var debug = true
  // The actor which triggers the simulation and gathers the steps
  private var solverAgent : ActorRef= context.parent

  // White page id/agent
  private var directory = new Directory()
  // Parent
  private var parent : Option[Variable] = None
  // Children
  private var children = Set[Variable]()
  /**
    * Message handling
    */
  override def receive: Receive = {
    // Initiation of the variable agent
    case Init(d, p, c) =>
      solverAgent = sender
      directory = d
      parent = p
      children = c
      solverAgent ! Valuation(variable.domain.head)

    // Debug mode
    case Trace =>
      debug = true
      directory.allActors().foreach(_ ! Trace)

    // Termination of the variable agent
    case Stop =>
      context.stop(self) //stops the solverAgent

    // Unexpected message
    case msg@_ =>
      println("WARNING: Solver receives a message which was not expected: " + msg)


  }

}
