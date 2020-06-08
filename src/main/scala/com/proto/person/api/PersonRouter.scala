package com.proto.person.api

import akka.http.scaladsl.server.Route
import com.proto.person.api.directive.PersonDirectives
import com.proto.person.api.validator.{PersonCreateValidator, PersonUpdateValidator, ValidatorDirectives}
import com.proto.person.domain.{CreatePerson, JsonSupport, UpdatePerson}
import com.proto.person.error.ApiError
import com.proto.person.persistence.PersonRepository

trait Router {
  def route: Route

}

class PersonRouter (personRepository : PersonRepository) extends Router
                                                        with JsonSupport
                                                        with PersonDirectives
                                                        with ValidatorDirectives {

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
          } { person => complete(person)}
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