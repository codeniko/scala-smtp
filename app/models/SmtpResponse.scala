package models

import play.api.libs.json.{Json, JsString, Writes}

sealed trait SmtpResponse

object SmtpResponse {
  implicit val jsonWrites: Writes[SmtpResponse] = new Writes[SmtpResponse] {
    def writes(r: SmtpResponse) = {
      val jsResult = r match {
        case SmtpSuccess => JsString("success")
        case _ => JsString("failure")
      }

      Json.obj("result" -> jsResult)
    }
  }
}

case object SmtpSuccess extends SmtpResponse
case object SmtpFailure extends SmtpResponse
