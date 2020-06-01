import scala.concurrent.Future

trait PersonMocks {
  class FailingRepository extends PersonRepository {
    override def all(): Future[Seq[Person]] = Future.failed(new Exception("Mocked exception"))

    override def adults(): Future[Seq[Person]] = Future.failed(new Exception("Mocked exception"))

    override def miners(): Future[Seq[Person]] = Future.failed(new Exception("Mocked exception"))
  }
}
