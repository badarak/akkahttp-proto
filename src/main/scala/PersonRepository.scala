import java.util.UUID

import PersonRepository.PersonNotFound

import scala.concurrent.{ExecutionContext, Future}

trait PersonRepository {
  def all(): Future[Seq[Person]]
  def adults() : Future[Seq[Person]]
  def miners() : Future[Seq[Person]]
  def save(createPerson: CreatePerson): Future[Person]
  def update(id : String, updatePerson: UpdatePerson) : Future[Person]
}

object PersonRepository {
  final case class PersonNotFound(id : String) extends Exception(s"Person with id $id not found.")
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

  override def update(id : String, updatePerson: UpdatePerson): Future[Person] = {
    initialListOfPerson.find(_.id == id) match {
      case Some(foundPerson) =>
        val newPerson = updatePersonHelper(foundPerson, updatePerson)
        persons = persons.map(p => if(p.id == id) newPerson else p)
        Future.successful(newPerson)
      case None =>
        Future.failed(PersonNotFound(id))
    }
  }

  private def updatePersonHelper(foundPerson: Person, updatePerson: UpdatePerson) : Person = {
    val t1 = updatePerson.firstName.map(firstName => foundPerson.copy(firstName = firstName))
                                   .getOrElse(foundPerson)
    val t2 = updatePerson.lastName.map(lastName => t1.copy(lastName =lastName))
                                  .getOrElse(t1)
    updatePerson.age.map(age => t2.copy(age = age)).getOrElse(t2)
  }
}
