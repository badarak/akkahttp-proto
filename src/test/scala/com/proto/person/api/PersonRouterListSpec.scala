package com.proto.person.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.proto.person.domain.{JsonSupport, Person}
import com.proto.person.error.ApiError
import com.proto.person.mock.PersonMocks
import com.proto.person.persistence.PersonRepositoryImpl
import com.proto.person.service.PersonService
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonRouterListSpec extends AnyWordSpec
                           with JsonSupport
                           with Matchers
                           with ScalatestRouteTest
                           with PersonMocks {


  private val adult: Person = Person("1", "Barak", "Obama", 58)
  private val miner: Person = Person("3", "Alain", "Pitt", 12)
  private val persons: Seq[Person] = Seq(adult, miner)

  val repository = new PersonRepositoryImpl(persons)
  val router: PersonRouter = new PersonRouter {
    override val personService: PersonService = new PersonService(repository)
  }

  "A com.proto.person.api.PersonRouter" should {

    "return all persons" in {

      Get("/persons") ~> router.personRoutes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Person]]
        resp shouldBe persons
      }
    }

    "return all miners" in {

      Get("/persons/miners") ~> router.personRoutes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Person]]
        resp shouldBe Seq(miner)
      }
    }

    "return all adults" in {

      Get("/persons/adults") ~> router.personRoutes ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Person]]
        resp shouldBe Seq(adult)
      }
    }

    val failingrepository = new FailingRepository()
    val failingRoute = new PersonRouter() {
      override val personService: PersonService = new PersonService(failingrepository)
    }

    "handle repository failure in the persons route" in {

      Get("/persons") ~> failingRoute.personRoutes ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }

    "handle repository failure in the miners route" in {

      Get("/persons/miners") ~> failingRoute.personRoutes ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }

    "handle repository failure in the adults route" in {

      Get("/persons/adults") ~> failingRoute.personRoutes ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
  }
}
