import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

trait PersonRepository {

  def all(): Future[Seq[Person]]
  def adults() : Future[Seq[Person]]
  def miners() : Future[Seq[Person]]

  def save(createPerson: CreatePerson): Future[Person]
}

class PersonRepositoryImpl(initialListOfPerson : Seq[Person] = Seq.empty)(implicit ec : ExecutionContext)
                          extends PersonRepository {
  var persons: Vector[Person] = initialListOfPerson.toVector

  override def all(): Future[Seq[Person]] = Future.successful(persons)

  override def adults(): Future[Seq[Person]] = Future.successful(persons.filter(_.age > 18))

  override def miners(): Future[Seq[Person]] = Future.successful(persons.filterNot(_.age > 18))

  override def save(createPerson: CreatePerson): Future[Person] = Future.successful {
    val person = Person(UUID.randomUUID().toString,
      createPerson.firstName,
      createPerson.lastName,
      createPerson.age)

    persons = persons :+ person
    person
  }
}
