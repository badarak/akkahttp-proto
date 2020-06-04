import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonRouterDeleteSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with PersonMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

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

  "A PersonRouter" should {

    "delete an existent person " in {

      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Delete(s"/persons/$personId") ~> router.route ~> check{
        status shouldBe StatusCodes.OK
        val resp = responseAs[Boolean]
        resp shouldBe true
      }
    }

    "return not found with delete non existent person" in {

      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Delete(s"/persons/5") ~> router.route ~> check{
        status shouldBe ApiError.personNotFound("5").statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.personNotFound("5").message
      }
    }

    "handle repository failure when deleting person" in {

      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Delete(s"/persons/$personId") ~> router.route ~> check{
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
 }
}
