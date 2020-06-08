package com.proto.person.api

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.proto.person.domain.{Person, UpdatePerson}
import com.proto.person.error.ApiError
import com.proto.person.mock.PersonMocks
import com.proto.person.persistence.PersonRepositoryImpl
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonRouterUpdateSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with PersonMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val personId = UUID.randomUUID().toString
  val testPerson = Person(
    personId,
    "Lionel",
    "Messi",
    32
  )
  val testUpdatePerson = UpdatePerson(
    Some("Barak"),
    None,
    Some(58)
  )

  "A com.proto.person.api.PersonRouter" should {

    "update a person with valid data" in {

      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Put(s"/persons/$personId", testUpdatePerson) ~> router.route ~> check{
        status shouldBe StatusCodes.OK
        val resp = responseAs[Person]
        resp.firstName shouldBe testUpdatePerson.firstName.get
        resp.lastName shouldBe testPerson.lastName
        resp.age shouldBe testUpdatePerson.age.get
      }
    }

    "return not found with non existent person" in {

      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Put(s"/persons/5", testUpdatePerson) ~> router.route ~> check{
        status shouldBe ApiError.personNotFound("5").statusCode

      }
    }

    "person not update with invalid lastName field" in {

      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Put(s"/persons/$personId", testUpdatePerson.copy(lastName = Some(""))) ~> router.route ~> check{
        status shouldBe ApiError.EmptyLastNameField.statusCode
      }
    }

    "person not update with invalid age field" in {

      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Put(s"/persons/$personId", testUpdatePerson.copy(age = Some(131))) ~> router.route ~> check{
        status shouldBe ApiError.InvalidAgeField.statusCode
      }
    }

    "handle repository failure when updating person" in {

      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Put(s"/persons/$personId", testPerson) ~> router.route ~> check{
        status shouldBe ApiError.generic.statusCode
      }
    }
  }
}