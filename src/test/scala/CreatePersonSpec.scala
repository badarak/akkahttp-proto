import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CreatePersonSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with PersonMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val persons = Seq(
    Person("1", "Barak", "Obama", 58),
    Person("2", "Michelle", "Obama", 56),
    Person("3", "Alain", "Pitt", 12),
    Person("4", "Lionel", "Messi", 32)
  )
  val person = CreatePerson("Edam", "Miler", 19)

  "A PersonRouter" should {

    "create a person with valid data" in {

      val personRepository = new PersonRepositoryImpl(persons)
      val router = new PersonRouter(personRepository)

      Post("/persons", person) ~> router.route ~> check{
        status shouldBe StatusCodes.OK
        val createdPerson = responseAs[Person]
        createdPerson.lastName shouldBe person.lastName
        createdPerson.age shouldBe person.age
      }
    }

    "person is not created with invalid lastName field" in {
      val createPerson = person.copy(lastName ="")
      val personRepository = new PersonRepositoryImpl(persons)
      val router = new PersonRouter(personRepository)

      Post("/persons", createPerson) ~> router.route ~> check{
        status shouldBe ApiError.EmptyLastNameField.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.EmptyLastNameField.message
      }
    }

    "person is not created with invalid age field" in {
      val createPerson = person.copy(age = 123)
      val personRepository = new PersonRepositoryImpl(persons)
      val router = new PersonRouter(personRepository)

      Post("/persons", createPerson) ~> router.route ~> check{
        status shouldBe ApiError.InvalidAgeField.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.InvalidAgeField.message
      }
    }

    "handle repository failure when updating todos" in {
      val personRepository = new FailingRepository
      val router = new PersonRouter(personRepository)

      Post(s"/persons", person) ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.generic.message
      }
    }
  }
}
