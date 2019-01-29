// Copyright (C) Maxime MORGE 2018
package org.adopt.util

import org.adopt.dfs.DFS
import org.adopt.problem._

/**
  * Main application
  */
object Main {
  def main(args: Array[String]): Unit = {
    if (! Toy4Example.pb.sound()) throw new RuntimeException("Pb is not sound")
    println(Toy4Example.pb)
    if (! Toy4Example.a1.sound()) throw new RuntimeException("A1 is not sound")
    println("A1: " + Toy4Example.a1)
    println("Objective: " + Toy4Example.a1.objective())
    if (! Toy4Example.a2.sound()) throw new RuntimeException("A1 is not sound")
    println("A2: " + Toy4Example.a2)
    println("Objective: " + Toy4Example.a2.objective())
    //val dfs =DFS(Toy4Example.pb,Toy4Example.x1)
    val dfs3 = new DFS(Toy4Example.x3)
    val dfs4 = new DFS(Toy4Example.x4)
    val dfs2 = new DFS(Toy4Example.x2)
    dfs2.children= Set(dfs3, dfs4)
    val dfs = new DFS(Toy4Example.x1)
    dfs.children = Set(dfs2)
    println("DFS: "+dfs)
    sys.exit(0)
  }
}
