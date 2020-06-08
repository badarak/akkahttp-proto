package com.proto.person.error

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

final case class ApiError(statusCode: StatusCode, message: String)

object ApiError {

  def apply(statusCode: StatusCode, message: String): ApiError = new ApiError(statusCode, message)
  val generic: ApiError = new ApiError(StatusCodes.InternalServerError, "Unknown error.")
  val EmptyLastNameField : ApiError = new ApiError(StatusCodes.BadRequest,
                                          "The lastNameField must not be empty.")

  val InvalidAgeField : ApiError = new ApiError(StatusCodes.BadRequest,
                                          "The age field must not be empty et must be valid number < 122")
  def personNotFound(id: String): ApiError = new ApiError(StatusCodes.BadRequest,
    "The person with the id $id not found.")

}




