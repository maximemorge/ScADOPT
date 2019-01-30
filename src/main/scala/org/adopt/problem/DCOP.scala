// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing a Distributed Constraint Optimization Problem
  * @param variables for which values must be given
  * @param constraints represented as cost functions
  */
class DCOP(val variables: Set[Variable], val constraints : List[Constraint]){

  /**
    * String representation
    */
  override def toString: String = s"Variables:\n"+
    variables.mkString("\t", "\n\t", "\n")+
  "Constraints:\n"+
    constraints.mkString("\t", "\n\t", "\n")

  /**
    * Returns the variables linked to v
    */
  def linked(v : Variable) :  List[Variable] = {
    var l =  List[Variable]()
    constraints.foreach{ c =>
      if (c.variable1 == v)  l = c.variable2 :: l
      if (c.variable2 == v)  l = c.variable1 :: l
    }
    l
  }

  /**
    * Returns true if the DCOP is sound, i.e.
    * 1 -there is at least one variable,
    * 2- each variable has a unique id
    * all the constraints are sound
    */
  def sound() : Boolean = variables.nonEmpty && variables.map(_.id).size == variables.size && constraints.forall( c => c.sound())

  /**
    * Returns the constraints over a specific variable
    */
  def constraints(variable: Variable) : List[Constraint] = constraints.filter(c => c.variable1 != variable && c.variable2 != variable)


}

