package com.proto.person

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import com.proto.person.api.Router

import scala.concurrent.Future

class Server (router: Router, host: String, port : Int)(implicit system : ActorSystem) {
  def bind(): Future[ServerBinding] = Http().bindAndHandle(router.route, host, port)
}
