// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * A domain for a variable is an ordered list of values
  */
package object domain {
  type Domain = List[_ <:Value]
}

/**
  * Abstract class for representing values of variables.
  */
abstract class Value{
  /**
    * Returns true if this value is equal to v
    */
  def equal(v: Value): Boolean

  /**
    * Returns a string representation of the value
    */
  override def toString: String
}

/**
  * Class for representing values of nominal variables, i.e.
  * variables whose domains are finite
  */
case class NominalValue(value : String) extends Value{
  /**
    * A secondary constructor.
    */
  def this(intVal: Int) {
    this(intVal.toString)
  }

  /**
    * Returns true if this value is equal to v
    */
  def equal(v: Value) : Boolean = {
    v match {
      case v : NominalValue =>  value == v.value
      case _ => false
    }
  }

  /**
    * Returns a string representation of the value
    */
  override def toString: String =  value
}

/**
  * Class for representing values of a boolean variable, i.e.
  * variables whose values are true and false
  */
case class BooleanValue(value : Boolean) extends Value{

  /**
    * Returns true if this value is equal to v
    */
  def equal(v: Value) : Boolean = {
    v match {
      case v : BooleanValue =>  value == v.value
      case _ => false
    }
  }

  /**
    * Returns a string representation of the value
    */
  override def toString: String = {
    if (value) return "True"
    "False"
  }
}