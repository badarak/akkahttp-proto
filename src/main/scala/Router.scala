import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

import scala.util.{Failure, Success}

trait Router {
  def route: Route

}

class PersonRouter (personRepository : PersonRepository) extends Router with Directives{
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("persons") {
    pathEndOrSingleSlash {
      get {
        onComplete(personRepository.all()){
          case Success(persons) => complete(persons)
          case Failure(error) => complete(StatusCodes.InternalServerError)
        }
      }
    }~ path("miners"){
      get {
        complete(personRepository.miners)
      }
    }~ path("adults"){
      get {
        complete(personRepository.adults)
      }
    }
  }
}

