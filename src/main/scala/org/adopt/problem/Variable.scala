// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

import domain.Domain

/**
  * Class representing a variable
  * @param id id of the variable
  * @param domains finite and and discrete set of value
  */
class Variable(val id: Int, val domain: Domain){

  /**
    * Nice string representation of the variable
    */
  override def toString : String = s"X$id in "+domain.mkString("[", ", ", "]")
}
