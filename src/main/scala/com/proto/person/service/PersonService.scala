package com.proto.person.service

import com.proto.person.domain.{CreatePerson, Person, UpdatePerson}
import com.proto.person.persistence.PersonRepository

import scala.concurrent.{ExecutionContext, Future}

class PersonService(personRepository: PersonRepository)(implicit val ec: ExecutionContext) {
  def all() : Future[Seq[Person]] = personRepository.all()
  def adults(): Future[Seq[Person]] = personRepository.adults()
  def miners(): Future[Seq[Person]] = personRepository.miners()

  def save(createPerson: CreatePerson): Future[Person] = personRepository.save(createPerson)
  def update(id: String, updatePerson: UpdatePerson): Future[Person] = personRepository.update(id, updatePerson)
  def delete(id: String): Future[Person] = personRepository.delete(id)

}

