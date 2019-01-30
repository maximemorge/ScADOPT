// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing a full assignment or a partial context
  * @param pb DCOP instance
  * @param valuation for each variable
  */
class Context(val pb : DCOP, var valuation : Map[Variable, Value]){

  /**
    * Secondary constructor
    */
  def this(pb : DCOP) = {
    this(pb, Map[Variable, Value]())
  }

  /**
    * Fix the value of a variable in the context
    */
  def fix(variable : Variable, value: Value) : Unit ={
    if (! variable.domain.contains(value))
      throw new RuntimeException(s"$variable=$value is forbidden since $value is not in ${variable.domain}")
      valuation += (variable -> value)
  }

  /**
    * Fix the values of variables in the context
    */
  def fix(values :  Map[Variable, Value]) : Unit ={
    values.foreach{ case (variable, value) =>
      fix(variable,value)
    }
  }

  /**
    * Returns true if the context is compatible with ctxt, i.e.
    * they do not disagree on any variable assignment
    */
  def isCompatible(ctxt : Context) : Boolean =
    this.valuation.keys.toSet.intersect(ctxt.valuation.keys.toSet).forall( variable => this.valuation(variable) == ctxt.valuation(variable))

  /**
    * Returns the objective value of the assignment
    */
  def objective() : Double = {
    var sum = 0.0
    pb.constraints.foreach{ c =>
      val index1 = c.variable1.index(valuation(c.variable1))
      val index2 = c.variable2.index(valuation(c.variable2))
      sum += c.cost(index1)(index2)
    }
    sum
  }

  /**
    * Returns true if the pb is sound,
    * i.e. each value is in the corresponding domain
    */
  def sound() : Boolean = {
    pb.variables.foreach{ variable =>
      if (! variable.domain.contains(valuation(variable))) return  false
    }
    true
  }

  /**
    * Returns true if the assignment is full
    */
  def isAssignment: Boolean = valuation.size == pb.variables.size

  /**
    * String representation of the assignment
    */
  override def toString: String = valuation.mkString("[",", ","]")
}
