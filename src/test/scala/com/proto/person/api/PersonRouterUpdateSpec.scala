package com.proto.person.api

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.proto.person.domain.{JsonSupport, Person, UpdatePerson}
import com.proto.person.error.ApiError
import com.proto.person.mock.PersonMocks
import com.proto.person.persistence.PersonRepositoryImpl
import com.proto.person.service.PersonService
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonRouterUpdateSpec extends AnyWordSpec
                             with Matchers
                             with ScalatestRouteTest
                             with PersonMocks
                             with JsonSupport{

  val personId: String = UUID.randomUUID().toString
  val testPerson: Person = Person(
    personId,
    "Lionel",
    "Messi",
    32
  )
  val testUpdatePerson: UpdatePerson = UpdatePerson(
    Some("Barak"),
    None,
    Some(58)
  )

  val repository = new PersonRepositoryImpl(Seq(testPerson))
  val router: PersonRouter = new PersonRouter {
    override val personService: PersonService = new PersonService(repository)
  }

  "A com.proto.person.api.PersonRouter" should {

    "update a person with valid data" in {

      Put(s"/persons/$personId", testUpdatePerson) ~> router.personRoutes ~> check{
        status shouldBe StatusCodes.OK
        val resp = responseAs[Person]
        resp.firstName shouldBe testUpdatePerson.firstName.get
        resp.lastName shouldBe testPerson.lastName
        resp.age shouldBe testUpdatePerson.age.get
      }
    }

    "return not found with non existent person" in {

      Put(s"/persons/5", testUpdatePerson) ~> router.personRoutes ~> check{
        status shouldBe ApiError.personNotFound("5").statusCode
      }
    }

    "person not update with invalid lastName field" in {

      Put(s"/persons/$personId", testUpdatePerson.copy(lastName = Some(""))) ~> router.personRoutes ~> check{
        status shouldBe ApiError.EmptyLastNameField.statusCode
      }
    }

    "person not update with invalid age field" in {

      Put(s"/persons/$personId", testUpdatePerson.copy(age = Some(131))) ~> router.personRoutes ~> check{
        status shouldBe ApiError.InvalidAgeField.statusCode
      }
    }

    val failingrepository = new FailingRepository()
    val failingRoute = new PersonRouter() {
      override val personService: PersonService = new PersonService(failingrepository)
    }
    "handle repository failure when updating person" in {

      Put(s"/persons/$personId", testPerson) ~> failingRoute.personRoutes ~> check{
        status shouldBe ApiError.generic.statusCode
      }
    }
  }
}
