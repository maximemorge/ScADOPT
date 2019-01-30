// Copyright (C) Maxime MORGE 2019
package org.adopt.dfs

import org.adopt.problem.{DCOP,Variable}

/**
  * Class representing a depth-first search tree for a DCOP
  * @param root variable
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
  // Is the variables are marked
  var marked :  Map[Variable,Boolean] = Map[Variable,Boolean]()

  /**
    * Factory's method
    * @param pb DCOP
    */
  def apply(pb: DCOP): DFS = {
    apply(pb,pb.variables.head)
  }

  /**
    * Factory's method
    * @param pb DCOP
    * @param root variable
  */
  def apply(pb: DCOP, root: Variable): DFS = {
    pb.variables.foreach { v =>
      marked += (v -> false)
    }
    buildDFS(pb, root)
  }

  /**
    * Returns a DFS which is recursively built
    */
  def buildDFS(pb: DCOP, root : Variable): DFS = {
    val dfs = new DFS(root)
    marked += (root -> true)
    pb.linked(root).foreach{ v => // for each linked variable
      if (! marked(v)) dfs.children += buildDFS(pb, v) // if the variable is not marked add subtree
    }
    dfs
  }
}