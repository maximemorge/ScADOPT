// Copyright (C) Maxime MORGE 2019
package org.adopt.dfs

import org.adopt.problem.{DCOP,Variable}

/**
  * Class representing a depth-first search tree for a DCOP
  */
class DFS(val root : Variable){
  var children : Set[DFS] = Set[DFS]()

  /**
    * String representation
    */
  override def toString: String = {
    root.id + children.mkString("[",",","]")
  }
}


/**
  * Factory for [[org.adopt.dfs.DFS]] instances
  */
object DFS{
  var marked :  Map[Variable,Boolean] = Map[Variable,Boolean]()

  /**
    * Factory's method
    */
  def apply(pb: DCOP, root: Variable): DFS = {
    pb.variables.foreach { v =>
      marked += (v -> false)
    }
    buildDFS(pb, root)
  }

  /**
    * A recursive implementation of DFS
    */
  def buildDFS(pb: DCOP, root : Variable): DFS = {
    val dfs = new DFS(root)
    marked += (root -> true)
    pb.linked(root).foreach{ v =>
      if (! marked(v)) dfs.children += buildDFS(pb, v)
    }
    dfs
  }
}

