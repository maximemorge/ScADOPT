// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing a binary constraint over two variables valuating the cost of each couple
  * @param variable1 first variable
  * @param variable2 second variable
  * @param cost matrix
  */
class Constraint(val variable1: Variable, val variable2: Variable, val cost : Array[Array[Double]]){

  /**
    * String representation
    */
  override def toString: String = {
  var r = s"Cost(X${variable1.id})(X${variable2.id})\n"
    for {
      i <-  variable1.domain.indices
      j <- variable2.domain.indices
    } r+=s"\t\tCost($i)($j) = ${cost(i)(j)}\n"
    r
  }

  /**
    * Returns true if the constraint is sound
    */
  def sound() : Boolean = {
    if (this.cost.length != variable1.domain.size) return false
    for {
      i <-  variable1.domain.indices
    } if (this.cost(i).length != variable1.domain.size) return false

    for {
      i <-  variable1.domain.indices
      j <- variable2.domain.indices
    } if (cost(i)(j) < 0.0) return false
  true
  }
}
