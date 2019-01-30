// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import org.adopt.problem.{Assignment, Variable, Value}

/**
  *  All possible messages between the actors
  */
abstract class Message

// Managing messages
// Debugs message
case object Trace extends  Message
// Start the solving
case object Start extends Message
// Initiates the variable agent with a directory, children, and eventually a parent
case class Init(directory : Directory, parent : Option[Variable], children: Set[Variable]) extends Message
// The variable agent is ready to start
case object Ready extends Message
// Gives the value of the variable agent
case class Valuation(value : Value) extends Message
// Give an assignment and the statistics
case class Outcome(assignment : Assignment) extends  Message
// Stop an agent
case object Stop extends Message


// DCOP messages
