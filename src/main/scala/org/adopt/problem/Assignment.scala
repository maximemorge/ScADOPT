// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing an assignment
  * @param pb DCOP instance
  * @param value for each variable
  */
class Assignment(val pb : DCOP, var value : Map[Variable, Value]){

  /**
    * Secondary constructor
    */
  def this(pb : DCOP) = {
    this(pb, Map[Variable, Value]())
  }

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
    * Returnw true if the pb is sound,
    * i.e. each value is in the corresponding domain
    */
  def sound() : Boolean = {
    pb.variables.foreach{ variable =>
      if (! variable.domain.contains(value(variable))) return  false
    }
    true
  }

  /**
    * Returns true if the assignment is full
    */
  def isFull: Boolean = value.size == pb.variables.size

  /**
    * String representation of the assignment
    */
  override def toString: String = value.mkString("[",", ","]")
}
