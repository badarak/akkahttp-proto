import akka.http.scaladsl.server.Route

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
      }~ post {
        entity(as[CreatePerson]) { createPerson =>
          validateWith(PersonCreateValidator)(createPerson) {
            handleWithGeneric(personRepository.save(createPerson)){ persons =>
              complete(persons)
            }
          }
        }
      }
    } ~ path(Segment){id : String =>
      put {
        entity(as[UpdatePerson]) { updatePerson =>
          validateWith(PersonUpdateValidator)(updatePerson){
            handle(personRepository.update(id, updatePerson))  {
              case PersonRepository.PersonNotFound(_) =>
                ApiError.personNotFound(id)
              case _ =>   ApiError.generic
            } {person => complete(person)}
          }
        }
      } ~ delete{
          handle(personRepository.delete(id))  {
            case PersonRepository.PersonNotFound(_) =>
              ApiError.personNotFound(id)
            case _ =>   ApiError.generic
          } { id => complete(id)}
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