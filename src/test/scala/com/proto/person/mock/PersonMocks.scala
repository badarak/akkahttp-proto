package com.proto.person.mock

import com.proto.person.domain.{CreatePerson, Person, UpdatePerson}
import com.proto.person.persistence.PersonRepository

import scala.concurrent.Future

trait PersonMocks {

  class FailingRepository extends PersonRepository {
    override def all(): Future[Seq[Person]] = Future.failed(new Exception("Mocked exception"))

    override def adults(): Future[Seq[Person]] = Future.failed(new Exception("Mocked exception"))

    override def miners(): Future[Seq[Person]] = Future.failed(new Exception("Mocked exception"))

    override def save(createPerson: CreatePerson): Future[Person] = Future.failed(new Exception("Mocked exception"))

    override def update(id: String, updatePerson: UpdatePerson): Future[Person] = Future.failed(new Exception("Mocked exception"))

    override def delete(id: String): Future[Person] = Future.failed(new Exception("Mocked exception"))
  }
}
