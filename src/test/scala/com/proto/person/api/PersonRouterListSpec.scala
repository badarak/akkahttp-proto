package com.proto.person.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.proto.person.domain.{JsonSupport, Person}
import com.proto.person.error.ApiError
import com.proto.person.mock.PersonMocks
import com.proto.person.persistence.PersonRepositoryImpl
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonRouterListSpec extends AnyWordSpec
                           with JsonSupport
                           with Matchers
                           with ScalatestRouteTest
                           with PersonMocks {


  private val adult = new Person("1", "Barak", "Obama", 58)
  private val miner = new Person("3", "Alain", "Pitt", 12)
  private val persons = Seq(adult, miner)


  "A com.proto.person.api.PersonRouter" should {

    "return all persons" in {
      val repository = new PersonRepositoryImpl(persons)
      val router = new PersonRouter(repository)

      Get("/persons") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Person]]
        resp shouldBe persons
      }
    }

    "return all miners" in {
      val repository = new PersonRepositoryImpl(persons)
      val router = new PersonRouter(repository)

      Get("/persons/miners") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Person]]
        resp shouldBe Seq(miner)
      }
    }

    "return all adults" in {
      val repository = new PersonRepositoryImpl(persons)
      val router = new PersonRouter(repository)

      Get("/persons/adults") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Person]]
        resp shouldBe Seq(adult)
      }
    }

    "handle repository failure in the persons route" in {
      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Get("/persons") ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }

    "handle repository failure in the miners route" in {
      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Get("/persons/miners") ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }

    "handle repository failure in the adults route" in {
      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Get("/persons/adults") ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
  }
}
