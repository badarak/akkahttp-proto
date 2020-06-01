import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PersonListSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with PersonMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val adult = new Person("1", "Barak", "Obama", 58)
  private val miner = new Person("3", "Alain", "Pitt", 12)
  private val persons = Seq(adult, miner)


  "A PersonRouter" should {

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
        status shouldBe StatusCodes.InternalServerError
      }
    }

    "handle repository failure in the miners route" in {
      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Get("/persons/miners") ~> router.route ~> check {
        status shouldBe StatusCodes.InternalServerError
      }
    }

    "handle repository failure in the adults route" in {
      val repository = new FailingRepository
      val router = new PersonRouter(repository)

      Get("/persons/adults") ~> router.route ~> check {
        status shouldBe StatusCodes.InternalServerError
      }
    }
  }
}
