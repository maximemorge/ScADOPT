// Copyright (C) Maxime MORGE 2018
package org.adopt.util

import akka.actor.ActorSystem
import org.adopt.dfs.DFS
import org.adopt.solver.DistributedSolver

/**
  * Main application to test ADOPT on a toy example
  */
object Main {
  import org.adopt.problem.ToyExample._
  def main(args: Array[String]): Unit = {
    if (! pb.sound()) throw new RuntimeException("Pb is not sound")
    println(pb)
    if (! a1.sound()) throw new RuntimeException("A1 is not sound")
    println("a1: " + a1)
    println("a1's objective: " + a1.objective())
    if (! a2.sound()) throw new RuntimeException("A2 is not sound")
    println("a2: " + a2)
    println("a2's objective: " + a2.objective())
    val dfs =DFS(pb,x1)
    println("DFS: "+dfs)
    val system = ActorSystem("TestDistributedSolver")
    val solver = new DistributedSolver(pb, system)
    val assignment = solver.run()
    println("Adopt outcome: " + assignment)
    println("Adopt outcome objective: " + assignment.objective())
    sys.exit(0)
  }
}
