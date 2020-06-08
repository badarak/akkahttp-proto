package com.proto.person.api.validator

import com.proto.person.domain.{CreatePerson, UpdatePerson}
import com.proto.person.error.ApiError

trait Validator[T]{
  def validate(t: T): Option[ApiError]
}

object PersonCreateValidator extends Validator[CreatePerson]{

  override def validate(createPerson: CreatePerson): Option[ApiError] =  {
    if(createPerson.lastName.isEmpty) Some(ApiError.EmptyLastNameField)
    else if (!createPerson.age.isValidInt || createPerson.age > 130) Some(ApiError.InvalidAgeField)
    else None
  }
}

object PersonUpdateValidator extends Validator[UpdatePerson]{

  override def validate(updatePerson: UpdatePerson): Option[ApiError] = {
    if(updatePerson.lastName.exists(_.isEmpty)) Some(ApiError.EmptyLastNameField)
    else if (updatePerson.age.exists(a => !(a.isValidInt) || a > 130)) Some(ApiError.InvalidAgeField)
    else None
  }
}
