
case class Person(id: String, firstName : String, lastName : String, age: Int)
case class CreatePerson(firstName : String, lastName : String, age: Int)
case class UpdatePerson(firstName : Option[String], lastName : Option[String], age: Option[Int])