// Copyright (C) Maxime MORGE 2019
package org.adopt.solver

import org.adopt.problem.{Context, DCOP}

/**
  * Abstract class representing a solver
  * @param pb DCOP instance
  */
abstract class Solver(val pb : DCOP) {
  var debug = false
  var solvingTime : Long = 0

  /**
    * Returns an assignment
    */
  protected def solve() : Context

  /**
    * Returns an assignment and update solving time
    */
  def run() : Context = {
    val startingTime = System.nanoTime()
    val assignment = solve()
    solvingTime = System.nanoTime() - startingTime
    if (! assignment.sound()) throw new RuntimeException(s"Solver: the outcome\n $assignment\n for\n $pb is not sound")
    assignment
  }

}
