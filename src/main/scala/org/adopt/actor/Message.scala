// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Context, Variable, Value}

/**
  *  All possible messages between the actors
  */
abstract class Message

// Managing messages
// Debuging message
case object Trace extends  Message
// Starts the solving
case object Start extends Message
// Initiates a variable agent with a directory, children, and eventually a parent
case class Init(directory : Directory, parent : Option[Variable], children: Set[Variable]) extends Message
// The variable agent is ready to start
case object Ready extends Message
// Gives an assignment and the statistics
case class Outcome(assignment : Context) extends  Message
// Stops an agent
case object Stop extends Message

// DCOP messages
// Fix the value of the variable agent
case class Assign(value : Value) extends Message
// Generalizes the nogood message of DisCSP
case class Cost(lb : Double, ub: Double, ctxt : Context) extends Message
// Reduces the redundant search
case class Threshold(threshold: Double) extends Message
