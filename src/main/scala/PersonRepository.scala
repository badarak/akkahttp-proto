import scala.concurrent.{ExecutionContext, Future}

trait PersonRepository {
  def all(): Future[Seq[Person]]
  def adults() : Future[Seq[Person]]
  def miners() : Future[Seq[Person]]
}

class PersonRepositoryImpl(initialListOfPerson : Seq[Person] = Seq.empty)(implicit ec : ExecutionContext)
                          extends PersonRepository {
  var persons : Vector[Person] = initialListOfPerson.toVector

  override def all(): Future[Seq[Person]] = Future.successful(persons)

  override def adults(): Future[Seq[Person]] = Future.successful(persons.filter(_.age > 18))

  override def miners(): Future[Seq[Person]] = Future.successful(persons.filterNot(_.age > 18))
}
