// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

import domain.Domain

/**
  * Class representing a variable
  * @param id id of the variable
  * @param domain finite and and discrete set of values
  */
class Variable(val id: Int, val domain: Domain){
  /**
    * String representation of the variable
    */
  override def toString : String = s"X$id in "+domain.mkString("[", ", ", "]")

  /**
    * Returns the index of a specific value
    */
  def index(value : Value) : Int = domain.indexOf(value)

  /**
    * Return true if the variable is sound
    */
  def sound() : Boolean = domain.nonEmpty
}

