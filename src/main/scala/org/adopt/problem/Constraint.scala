// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing a constraint over two variables qui valuate the cost of each couple
  */
class Constraint(val variable1: Variable, val variable2: Variable){

  var cost = Array.ofDim[Double](variable1.domain.size, variable1.domain.size)
  for {
    i <- 0 until variable1.domain.size
    j <- 0 until variable2.domain.size
  } cost(i)(j) = 0.0

  /**
    * A secondary constructor.
    */
  def this(variable1: Variable, variable2: Variable, cost : Array[Array[Double]]) {
    this(variable1, variable2)
    this.cost = cost
  }

  /**
    * Nice string representation
    */
  override def toString: String = {
  var r = s"Cost(X${variable1.id})(X${variable2.id})\n"
    for {
      i <- 0 until variable1.domain.size
      j <- 0 until variable2.domain.size
    } r+=s"\t\tCost($i)($j) = ${cost(i)(j)}\n"
    r
  }
}
