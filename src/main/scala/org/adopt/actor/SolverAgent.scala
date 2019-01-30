// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Context, DCOP, Variable}
import org.adopt.dfs.DFS

import akka.actor.{Actor, ActorRef, Props}

/**
  * SolverAgent which starts and stops the computation of an assignment
  * @param pb DCOP instance
  * */
class SolverAgent(val pb: DCOP) extends Actor{
  var debug = true
  // The actor which triggers the simulation and gathers the steps
  private var solver : ActorRef= context.parent
  // Number of agents which are ready
  private var nbReady = 0
  // Is the solver agent started
  var started = false
  // White page variable/actor
  private val directory = new Directory()
  // The assignment to build
  private val assignment = new Context(pb)

  // Deploy and init
  pb.variables.foreach{ variable =>
    val actor = context.actorOf(Props(classOf[VariableAgent], variable, pb), variable.id.toString)
    directory.add(variable, actor) // Add it to the directory
  }
  init(DFS(pb), None)

  /**
    *  Returns the root variable of the DFS whose children have been initiated
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
    // When a variable agent is ready
    case Ready =>
      nbReady += 1
      if (nbReady == pb.variables.size && started) directory.allActors().foreach(_ ! Start)

    //When debugging mode is triggered
    case Trace =>
      debug = true
      directory.allActors().foreach(_ ! Trace)

    //When the solving is triggered
    case Start =>
      solver = sender
      started = true
      if (nbReady == pb.variables.size ) directory.allActors().foreach(_ ! Start)

    //When the value of variable is setup
    case Assign(value) =>
        assignment.valuation += (directory.variables(sender) -> value)
        if (assignment.isAssignment){
          solver ! Outcome(assignment) // reports the allocation
          directory.allActors().foreach(a => a ! Stop) // stops the actors
          context.stop(self) //stops the solverAgent
        }

    // Unexpected message
    case msg@_ =>
      println("WARNING: SolverAgent receives a message which was not expected: " + msg)
  }

}
