import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

import scala.util.{Failure, Success}

trait Router {
  def route: Route

}

class PersonRouter (personRepository : PersonRepository) extends Router with PersonDirectives with ValidatorDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("persons") {
    pathEndOrSingleSlash {
      get {
        handleWithGeneric(personRepository.all()) {persons =>
          complete(persons)
        }
      } ~ post {
        entity(as[CreatePerson]) { createPerson =>
          validateWith(PersonCreateValidator)(createPerson) {
            handleWithGeneric(personRepository.save(createPerson)){ person =>
              complete(person)
            }
          }
        }
      }
    }~ path("miners"){
      get {
        handleWithGeneric(personRepository.miners){persons =>
          complete(persons)
        }
      }
    }~ path("adults"){
      get {
        handleWithGeneric(personRepository.adults){persons =>
          complete(persons)
        }
      }
    }
  }
}