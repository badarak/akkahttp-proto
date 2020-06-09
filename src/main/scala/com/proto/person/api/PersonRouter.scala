package com.proto.person.api

import akka.http.scaladsl.server.Route
import com.proto.person.api.directive.PersonDirectives
import com.proto.person.api.validator.{PersonCreateValidator, PersonUpdateValidator, ValidatorDirectives}
import com.proto.person.domain.{CreatePerson, JsonSupport, UpdatePerson}
import com.proto.person.error.ApiError
import com.proto.person.persistence.PersonRepository
import com.proto.person.service.PersonService


trait PersonRouter extends JsonSupport
                   with PersonDirectives
                   with ValidatorDirectives {

  val personService : PersonService

  def personRoutes: Route = pathPrefix("persons") {
    pathEndOrSingleSlash {
      get {
        handleWithGeneric(personService.all()) {persons =>
          complete(persons)
        }
      }~ post {
        entity(as[CreatePerson]) { createPerson =>
          validateWith(PersonCreateValidator)(createPerson) {
            handleWithGeneric(personService.save(createPerson)){ persons =>
              complete(persons)
            }
          }
        }
      }
    } ~ path(Segment){id : String =>
      put {
        entity(as[UpdatePerson]) { updatePerson =>
          validateWith(PersonUpdateValidator)(updatePerson){
            handle(personService.update(id, updatePerson))  {
              case PersonRepository.PersonNotFound(_) =>
                ApiError.personNotFound(id)
              case _ =>   ApiError.generic
            } {person => complete(person)}
          }
        }
      } ~ delete{
          handle(personService.delete(id))  {
            case PersonRepository.PersonNotFound(_) =>
              ApiError.personNotFound(id)
            case _ =>   ApiError.generic
          } { person => complete(person)}
      }
    }~ path("miners"){
      get {
        handleWithGeneric(personService.miners()){persons =>
          complete(persons)
        }
      }
    }~ path("adults"){
      get {
        handleWithGeneric(personService.adults()){persons =>
          complete(persons)
        }
      }
    }
  }
}