import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonRouterCreateSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with PersonMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val testPerson = Person("1", "Barak", "Obama", 58)

  val testCreatePerson = CreatePerson("Edam", "Miler", 19)


  "A PersonRouter" should {

    "create a person with valid data" in {

      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Post("/persons", testCreatePerson) ~> router.route ~> check{
        status shouldBe StatusCodes.OK
        val resp = responseAs[Person]
        resp.lastName shouldBe testCreatePerson.lastName
        resp.age shouldBe testCreatePerson.age
      }
    }

    "person is not created with invalid lastName field" in {
      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Post("/persons", testCreatePerson.copy(lastName = "")) ~> router.route ~> check{
        status shouldBe ApiError.EmptyLastNameField.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.EmptyLastNameField.message
      }
    }

    "person is not created with invalid age field" in {
      val repository = new PersonRepositoryImpl(Seq(testPerson))
      val router = new PersonRouter(repository)

      Post("/persons", testCreatePerson.copy(age = 131)) ~> router.route ~> check{
        status shouldBe ApiError.InvalidAgeField.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.InvalidAgeField.message
      }
    }

    "handle repository failure when updating todos" in {
      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Post(s"/persons", testCreatePerson) ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
  }
}
