// Copyright (C) Maxime MORGE 2019
package org.adopt.solver

import org.adopt.problem.{Assignment, DCOP}

/**
  * Abstract class representing a solver
  * @param pb DCOP to be solved
  */
abstract class Solver(val pb : DCOP) {
  var debug = false

  var solvingTime : Long = 0

  /**
    * Returns an assignment
    */
  protected def solve() : Assignment

  /**
    * Returns an assignment and update solving time
    */
  def run() : Assignment = {
    val startingTime = System.nanoTime()
    val assignment = solve()
    solvingTime = System.nanoTime() - startingTime
    if (! assignment.sound()) throw new RuntimeException(s"Solver: the outcome\n $assignment\n for\n $pb is not sound")
    assignment
  }

}
