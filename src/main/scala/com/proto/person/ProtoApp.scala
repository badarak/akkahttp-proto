package com.proto.person

import akka.actor.ActorSystem
import com.proto.person.api.PersonRouter
import com.proto.person.domain.Person
import com.proto.person.persistence.PersonRepositoryImpl

import scala.concurrent.Await
import scala.util.{Failure, Success}

object ProtoApp  extends App {
  val host = "0.0.0.0"
  val port = 9000

  implicit val system : ActorSystem = ActorSystem(name = "personapi")
  implicit val executionContext = system.dispatcher

  val personRepository = new PersonRepositoryImpl(Seq(
    Person("1", "Barak", "Obama", 58),
    Person("2", "Michelle", "Obama", 56),
    Person("3", "Alain", "Pitt", 12),
    Person("4", "Lionel", "Messi", 32)
  ))

  val router  = new PersonRouter(personRepository)
  val server  = new Server(router, host, port)

  val binding = server.bind()
  binding.onComplete{
    case Success(_) =>
      system.log.info("Server online at http://{}:{}/", host, port)
    case Failure(error) =>
      system.log.error("Failed to bind HTTP endpoint, terminating system", error)
      system.terminate()
  }

  import scala.concurrent.duration._
  Await.result(binding, 3.seconds)
}
