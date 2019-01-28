// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Class representing a Distributed Constraint Optimization Problem
  * @param variables for which values must be given
  * @param constraints represented as cost functions
  */
class DCOP(val variables: Set[Variable], constraints : Set[Constraint]){

  /**
    * Nice representation as a string
    */
  override def toString: String = s"Variables:\n"+
    variables.mkString("\t", "\n\t", "\n")+
  "Constraints:\n"+
    constraints.mkString("\t", "\n\t", "\n")

}

