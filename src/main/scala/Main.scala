import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.concurrent.Await
import scala.util.{Failure, Success}

object Main  extends App {
  val host = "0.0.0.0"
  val port = 9000

  implicit val system : ActorSystem = ActorSystem(name = "personapi")
  import system.dispatcher


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
    case Success(_) => print("Server Up")
    case Failure(error) => print(s"Failed: ${error.getMessage}")
  }

  import scala.concurrent.duration._
  Await.result(binding, 3.seconds)
}
