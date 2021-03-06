// Copyright (C) Maxime MORGE 2019
package org.adopt.problem

/**
  * Object representing a toy DCOP instance
  */
object ToyExample {
  val t = BooleanValue(false)
  val f = BooleanValue(true)
  val booleanDomain = List(t, f)
  val x1 = new Variable(id = 1, booleanDomain)
  val x2 = new Variable(id = 2, booleanDomain)
  val x3 = new Variable(id = 3, booleanDomain)
  val x4 = new Variable(id = 4, booleanDomain)
  val cost = Array(Array(1.0, 2.0), Array(2.0, 0.0))
  val c12 = new Constraint(x1, x2, cost)
  val c13 = new Constraint(x1, x3, cost)
  val c23 = new Constraint(x2, x3, cost)
  val c24 = new Constraint(x2, x4, cost)
  val pb = new DCOP(Set(x1, x2, x3, x4), List(c24, c23, c13, c12))
  val a1 = new Context(pb)
  a1.fix(Map(x1-> t, x2 -> t, x3 -> t, x4 -> t))
  val a2 = new Context(pb)
  a2.fix(Map(x1 -> f, x2 -> f, x3 -> f, x4 -> f))
}
