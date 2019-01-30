// Copyright (C) Maxime MORGE 2019
package org.adopt.actor

import akka.actor.ActorRef
import org.adopt.problem.Variable

/**
  * Class representing an index of the names and addresses of peers
  */
class Directory {
  var adr : Map[Variable, ActorRef] = Map[Variable, ActorRef]()//Variables' actors
  var variables : Map[ActorRef, Variable] = Map[ActorRef, Variable]()// Actors' variable

  /**
    * String representation
    */
  override def toString: String = allVariables().mkString("[",", ","]")

  /**
    * Add to the directory
    * @param variable to add
    * @param ref to add
    */
  def add(variable: Variable, ref: ActorRef) : Unit = {
    if ( ! adr.keySet.contains(variable) &&  ! variables.keySet.contains(ref)) {
      adr += (variable -> ref)
      variables += (ref -> variable)
    }
    else throw new RuntimeException(s"$variable and/or $ref already in the directory")
  }

  def allActors() : Iterable[ActorRef]  = adr.values
  def allVariables() : Iterable[Variable]  = variables.values
  def peers(variable: Variable) : Set[Variable] = allVariables().filterNot(_ ==variable).toSet
  def peersActor(variable: Variable) :  Iterable[ActorRef] = peers(variable: Variable).map(w => adr(w))

}

