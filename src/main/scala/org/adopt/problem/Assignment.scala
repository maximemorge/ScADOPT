// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing an assignment
  * @param pb DCOP
  * @param value for each variable
  */
class Assignment(val pb : DCOP, value : Map[Variable, Value]){
  /**
    * Returns the objective value of the assignment
    */
  def objective() : Double = {
    var sum = 0.0
    pb.constraints.foreach{ c =>
      val index1 = c.variable1.index(value(c.variable1))
      val index2 = c.variable2.index(value(c.variable2))
      sum += c.cost(index1)(index2)
    }
    sum
  }

  /**
    * Return true if the pb is sound
    */
  def sound() : Boolean = {
    pb.variables.foreach{ variable =>
      if (! variable.domain.contains(value(variable))) return  false
    }
    true
  }

  /**
    * String representation of the assignment
    */
  override def toString: String = value.mkString("[",", ","]")
}
