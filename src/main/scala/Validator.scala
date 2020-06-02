import scala.concurrent.Future

trait Validator[T]{
  def validate(t: T): Option[ApiError]
}

object PersonCreateValidator extends Validator[CreatePerson]{

  override def validate(createPerson: CreatePerson): Option[ApiError] =  {
    if(createPerson.lastName.isEmpty) Some(ApiError.EmptyLastNameField)
    else if (!createPerson.age.isValidInt || createPerson.age > 122) Some(ApiError.InvalidAgeField)
    else None
  }
}
