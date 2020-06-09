package com.proto

import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import com.proto.person.persistence.PersonRepositoryImpl
import com.proto.person.service.PersonService

trait Modules {

  def _system: ActorSystem

  lazy val executionContext: MessageDispatcher = _system.dispatchers.lookup("akka-dispatcher")

  lazy val personRepository = {
    new PersonRepositoryImpl()(executionContext)
  }

  lazy val personService = {
    new PersonService(personRepository)(executionContext)
  }
}
