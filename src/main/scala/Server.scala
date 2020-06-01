import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

import scala.concurrent.Future

class Server (router: Router, host: String, port : Int)(implicit system : ActorSystem) {
  def bind(): Future[ServerBinding] = Http().bindAndHandle(router.route, host, port)
}
