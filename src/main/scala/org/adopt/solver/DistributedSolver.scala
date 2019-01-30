// Copyright (C) Maxime MORGE 2019
package org.adopt.solver

import org.adopt.problem.{DCOP,Assignment}
import org.adopt.actor.{SolverAgent,Trace,Outcome,Start}

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * Distributed solver based on multi-agent negotiation process of single gift in order to minimize the rule
  * @param pb to be solver
  * @param system of Actors
  */
class DistributedSolver(pb : DCOP, system: ActorSystem) extends Solver(pb) {

  val TIMEOUTVALUE : FiniteDuration = 6000 minutes // Default timeout of a run
  implicit val timeout : Timeout = Timeout(TIMEOUTVALUE)
  // Launch a new solverAgent
  DistributedSolver.id+=1
  val supervisor : ActorRef = system.actorOf(Props(classOf[SolverAgent], pb), name = "solverAgent"+DistributedSolver.id)

  //TODO nb messages

  /**
    * Returns an allocation
    */
  override def solve(): Assignment = {
    if (debug) println("@startuml")
    if (debug) println("skinparam monochrome true")
    if (debug) println("hide footbox")
    if (debug) {
      for (i<- 1 to pb.variables.size) println(s"participant $i")
    }
    if (debug) supervisor ! Trace
    val future = supervisor ? Start
    val result = Await.result(future, timeout.duration).asInstanceOf[Outcome]
    if (debug) println("@enduml")
    //TODO count message
    result.assignment
  }
}

object DistributedSolver{
  var id = 0
  val debug = false
  def main(args: Array[String]): Unit = {
    import org.adopt.problem.ToyExample._
    println(pb)
    val r = scala.util.Random
    val system = ActorSystem("DistributedSolver" + r.nextInt.toString)
    //The Actor system
    val adoptSolver = new DistributedSolver(pb, system)// SingleGiftOnly SingleSwapAndSingleGift
    adoptSolver.debug = true
    val sol = adoptSolver.solve()
    println(sol.toString)
    println("Objective "+sol.objective())
    System.exit(1)
  }
}
