// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Assignment, DCOP, Variable}
import org.adopt.dfs.DFS

import akka.actor.{Actor, ActorRef, Props}

/**
  * SolverAgent which starts and stops the computation of an allocation
  * @param pb DCOP problem instance
  * */
class SolverAgent(val pb: DCOP) extends Actor{
  var debug = true
  // The actor which triggers the simulation and gathers the steps
  private var solver : ActorRef= context.parent
  // Number of agents which are ready
  private var nbReady = 0
  // Is the solver agent started ?
  var started = false
  // White page id/agent
  private val directory = new Directory()
  // The assignment to build
  private val assignment = new Assignment(pb)

  // Deploy and init
  pb.variables.foreach{ variable =>
    val actor = context.actorOf(Props(classOf[VariableAgent], variable, pb), variable.id.toString)
    directory.add(variable, actor) // Add it to the directory
  }
  init(DFS(pb), None)

  /**
    *  Initiation of the agents with the directory, the parent and the children
    */
  def init(dfs: DFS, parent: Option[Variable]) : Variable = {
    var children = Set[Variable]()
    dfs.children.foreach{ dfs =>
      children += init(dfs,Some(dfs.root))
    }
    directory.adr(dfs.root) ! Init(directory, parent, children)
    dfs.root
  }

  /**
    * Message handling
    */
  override def receive: Receive = {

    case Ready =>
      nbReady += 1
      if (nbReady == pb.variables.size && started) directory.allActors().foreach(_ ! Start)

    //In order to debug
    case Trace =>
      debug = true
      directory.allActors().foreach(_ ! Trace)

    //When the works should be done
    case Start =>
      solver = sender
      started = true
      if (nbReady == pb.variables.size ) directory.allActors().foreach(_ ! Start)


    case Valuation(value) =>
        assignment.value += (directory.variables(sender) -> value)
        if (assignment.isFull()){
          solver ! Outcome(assignment) // reports the allocation
          directory.allActors().foreach(a => a ! Stop) // stops the actors
          context.stop(self) //stops the solverAgent
        }

    // Unexpected message
    case msg@_ =>
      println("WARNING: Solver receives a message which was not expected: " + msg)
  }

}
