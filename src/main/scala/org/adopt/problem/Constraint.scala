// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing a binary constraint over two variables
  * which valuates the cost of each couple of values
  * @param variable1 the first variable
  * @param variable2 the second variable
  * @param cost the cost matrix
  */
class Constraint(val variable1: Variable, val variable2: Variable, val cost : Array[Array[Double]]){

  /**
    * String representation
    */
  override def toString: String = {
  var r = s"Cost(x${variable1.id})(x${variable2.id})\n"
    for {
      i <- variable1.domain.indices
      j <- variable2.domain.indices
    } r+=s"\t\tCost($i)($j) = ${cost(i)(j)}\n"
    r
  }

  /**
    * Returns true if the constraint is sound, i.e
    * the cost of each couple of values is known and positive
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

  /**
    * Returns true if the constraint is over a specific variable
    */
  def isOver(variable: Variable) : Boolean = variable == variable1 || variable == variable2

  /**
    * Returns the cost when
    * variableA is assigned to valueA
    * and variableB is assigned to valueB
    */
  def cost(variableA: Variable, valueA: Value, variableB: Variable, valueB: Value) : Double = {
    var (index1,index2) = (0,0)
    if (variableA == variable1) {
      index1 = variable1.index(valueA)
      index2 = variable2.index(valueB)
    } else if (variableA == variable2) {
      index2 = variable1.index(valueA)
      index1 = variable2.index(valueB)
    } else throw new RuntimeException(s"Constraint $this is not about $variableA and $variableB")
    cost(index1)(index2)
  }

}
