package com.proto

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.proto.person.api.PersonRouter

import scala.util.{Failure, Success}

object ProtoApp  extends App with ServerConfig {

  implicit val system : ActorSystem = ActorSystem(name = "personapi")
  implicit val executionContext = system.dispatcher

  val modules = new Modules with PersonRouter {
    override def _system: ActorSystem = system
  }

  val route = modules.personRoutes

  val binding = Http().bindAndHandle(route, host, port)
  binding.onComplete {
    case Success(_) =>
      system.log.info("Server online at http://{}:{}/", host, port)
    case Failure(error) =>
      system.log.error("Failed to bind HTTP endpoint, terminating system", error)
      system.terminate()
  }
}
