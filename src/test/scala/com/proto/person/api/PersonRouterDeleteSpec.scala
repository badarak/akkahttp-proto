package com.proto.person.api

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.proto.person.domain.{JsonSupport, Person}
import com.proto.person.error.ApiError
import com.proto.person.mock.PersonMocks
import com.proto.person.persistence.PersonRepositoryImpl
import com.proto.person.service.PersonService
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonRouterDeleteSpec extends AnyWordSpec
                             with Matchers
                             with ScalatestRouteTest
                             with PersonMocks
                             with  JsonSupport {

  val personId: String = UUID.randomUUID().toString
  val testPerson: Person = Person(
    personId,
    "Lionel",
    "Messi",
    32
  )

  val repository = new PersonRepositoryImpl(Seq(testPerson))
  val router: PersonRouter = new PersonRouter {
    override val personService: PersonService = new PersonService(repository)
  }

  "A com.proto.person.api.PersonRouter" should {

    "delete an existent person " in {

      Delete(s"/persons/$personId") ~> router.personRoutes ~> check{
        status shouldBe StatusCodes.OK
        val resp = responseAs[Person]
        resp.id shouldBe testPerson.id
        resp.firstName shouldBe testPerson.firstName
        resp.lastName shouldBe testPerson.lastName
      }
    }

    "return not found with delete non existent person" in {

      Delete(s"/persons/5") ~> router.personRoutes ~> check{
        status shouldBe ApiError.personNotFound("5").statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.personNotFound("5").message
      }
    }

    val failingrepository = new FailingRepository()
    val failingRoute = new PersonRouter() {
      override val personService: PersonService = new PersonService(failingrepository)
    }

    "handle repository failure when deleting person" in {

      Delete(s"/persons/$personId") ~> failingRoute.personRoutes ~> check{
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
 }
}
