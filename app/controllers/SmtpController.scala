package controllers

import java.util.UUID
import javax.inject.Inject

import com.google.inject.Singleton
import com.google.inject.name.Named
import log.ClassLogger
import org.slf4j.MDC
import play.api.Configuration
import play.api.mvc.{Action, AnyContent, AbstractController, Headers, ControllerComponents, BodyParser, Results, Result}
import play.api.libs.json.{Json, JsValue}
import models._

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

import courier._

@Singleton
class SmtpController @Inject()(cfg: Configuration, cc: ControllerComponents)(implicit @Named("mdcEC") ec: ExecutionContext) extends AbstractController(cc) with ClassLogger {
  private val traceIdHeader = "TraceId"

  private val user = cfg.get[String]("smtp.user")

  private val mailer = Mailer(cfg.get[String]("smtp.server"), cfg.get[Int]("smtp.port"))
    .auth(true)
    .as(user, cfg.get[String]("smtp.pass"))
    .startTtls(cfg.get[Boolean]("smtp.tls"))()


  implicit def toResult(response: Future[SmtpResponse]): Future[Result] = response.map(r => Ok(Json.toJson(r)))


  private def addDebugLogFields(headers: Headers) {
    val headersMap: Map[String, Seq[String]] = headers.toMap
    val traceId: Option[String] = headersMap.get(traceIdHeader).fold(None: Option[String])(_.headOption)

    MDC.put("traceId", traceId.getOrElse(UUID.randomUUID().toString))
  }

  def sendMail(): Action[JsValue] = Action.async(parse.tolerantJson) { request =>
    try {
      addDebugLogFields(request.headers)
      logger.debug(s"Received POST request=$request")

      val response: Future[SmtpResponse] = request.body.validate[SmtpRequest].fold(e => {
        logger.error(s"Failed to validate request: $e")
        Future.successful(SmtpFailure: SmtpResponse)
      }, smtpRequest => {
        logger.debug(s"validated request: $smtpRequest")

        val content = s"From: ${smtpRequest.from}\n\n${smtpRequest.message}"

        val eventualMailResult = mailer(Envelope.from(user.addr)
          .to(cfg.get[String]("toEmail").addr)
          .subject(smtpRequest.subject)
          .content(Text(content)))

        eventualMailResult.map(_ => {
          logger.debug("message delivered")
          SmtpSuccess
        })

      })

      response

    } finally {
      MDC.clear()
    }
  }
}
