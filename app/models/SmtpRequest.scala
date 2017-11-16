package models

import play.api.libs.json.{Json, OFormat}

final case class SmtpRequest(from: String, subject: String, message: String, ip: Option[String])

object SmtpRequest {
  implicit val format: OFormat[SmtpRequest] = Json.format[SmtpRequest]
}
