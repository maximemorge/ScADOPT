// Copyright (C) Maxime MORGE 2018
package org.adopt.util

import akka.actor.ActorSystem
import org.adopt.dfs.DFS
import org.adopt.solver.DistributedSolver

/**
  * Main application
  */
object Main {
  import org.adopt.problem.Toy4Example._
  def main(args: Array[String]): Unit = {
    if (! pb.sound()) throw new RuntimeException("Pb is not sound")
    println(pb)
    if (! a1.sound()) throw new RuntimeException("A1 is not sound")
    println("A1: " + a1)
    println("Objective: " + a1.objective())
    if (! a2.sound()) throw new RuntimeException("A1 is not sound")
    println("A2: " + a2)
    println("Objective: " + a2.objective())
    val dfs =DFS(pb,x1)
    println("DFS: "+dfs)

    val system = ActorSystem("TestDistributedSolver")
    val solver = new DistributedSolver(pb, system)
    val assignment = solver.run()
    println("Adopt outcome: " + assignment)
    println("Objective: " + assignment.objective())
    sys.exit(0)
  }
}
