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
    * Returns the variables links to v
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
    * Return true if the pb is sound
    */
  def sound() : Boolean = variables.nonEmpty && variables.map(_.id).size == variables.size && constraints.forall( c => c.sound())
}

