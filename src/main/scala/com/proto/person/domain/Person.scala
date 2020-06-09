package com.proto.person.domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


case class Person(id: String, firstName : String, lastName : String, age: Int)
case class CreatePerson(firstName : String, lastName : String, age: Int)
case class UpdatePerson(firstName : Option[String], lastName : Option[String], age: Option[Int])


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol{

  implicit val personFormat = jsonFormat4(Person)
  implicit val createPersonFormat = jsonFormat3(CreatePerson)
  implicit val updatePersonFormat = jsonFormat3(UpdatePerson)
}