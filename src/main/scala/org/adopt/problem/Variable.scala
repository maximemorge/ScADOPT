// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

import domain.Domain

/**
  * Class representing a variable
  * @param id is the id of the variable
  * @param domain is a finite and and discrete set of values
  */
class Variable(val id: Int, val domain: Domain){
  /**
    * Long string representation of the variable
    */
  def description : String = s"x$id in "+domain.mkString("[", ", ", "]")

  /**
    * Short string representation of the variable
    */
  override def toString : String = s"x$id"

  /**
    * Returns the index of a specific value
    */
  def index(value : Value) : Int = domain.indexOf(value)

  /**
    * Return true if the variable is sound, i.e. a non-empty domain
    */
  def sound() : Boolean = domain.nonEmpty
}

