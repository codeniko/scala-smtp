package controllers

import java.net.InetAddress
import java.time.{Clock, OffsetDateTime}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import log.ClassLogger
import play.api.Configuration
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, AbstractController, ControllerComponents}
import play.api.mvc._

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext

@Singleton
class HealthCheckController @Inject()(wsClient: WSClient, cfg: Configuration, clock: Clock, cc: ControllerComponents)(implicit @Named("mdcEC") ec: ExecutionContext) extends AbstractController(cc) {
  private def systemValue(key : String) = Option(System.getenv.get(key)).filter(_.nonEmpty)

  private val physicalHostname = Option(InetAddress.getLocalHost.getHostName).filter(_.nonEmpty)
  private val logicalHostname = physicalHostname.getOrElse("")

  private def getHealthOfDependencies(dependencies: Seq[HealthCheckDependency]): Future[Map[String, JsObject]] = {
    Future.sequence(dependencies.map(_.fetch))
      .map(_.map(info ⇒
        (info.name, Json.obj(
          "healthy" → info.status.healthy,
          "message" → info.status.message,
          "url" → info.status.url,
          "time_taken" → info.time_taken
        ))
      ).toMap)
  }

  def healthCheck() = Action.async { implicit request =>
    for {
      dependencies <- getHealthOfDependencies(Seq(
        //new SelfHealthCheck(wsClient),
        ))
    } yield {
      Ok(Json.obj(
        "healthy" → dependencies.forall { case (_, obj) ⇒ (obj \ "healthy").asOpt[Boolean].getOrElse(false)},
        "time" → OffsetDateTime.now(clock).toString,
        "logical_host" → logicalHostname
      ) ++ JsObject(dependencies)
      )
    }

  }
}


final case class ComponentStatus(healthy: Boolean, message: String, url: String)
final case class ComponentInfo(name: String, status: ComponentStatus, time_taken: Long)


abstract class HealthCheckDependency(name: String)(implicit request: Request[AnyContent], ec: ExecutionContext) extends ClassLogger {
  import enhancements.FutureWithTimeout._

  val url: String
  val timeout = 2

  def check: Future[ComponentStatus]

  def fetch: Future[ComponentInfo] = {
    val start = System.currentTimeMillis()

    logger.debug(s"calling health check URL for component $name: $url")

    check.map(status => {
      val stop = System.currentTimeMillis()
      ComponentInfo(name, status, stop - start)
    }).withTimeout(FiniteDuration(timeout, TimeUnit.SECONDS))

      .recover {
        case NonFatal(e) ⇒
          val stop = System.currentTimeMillis()
          ComponentInfo(name, ComponentStatus(healthy = false, e.getLocalizedMessage, url), stop - start)
      }

  }
}

/*
class SelfHealthCheck(wsClient: WSClient)(implicit request: Request[AnyContent]) extends HealthCheckDependency("self")(request) {
  override val url: String = {
    // Use an absolute URL to test the network connection (not using localhost).
    routes.BotController.replyV1(":health?", "health-check-user", None, None).absoluteURL()
  }

  override def check: Future[ComponentStatus] = {
    wsClient.url(url).get().map(response => {

      val healthy = response.status == 200
      ComponentStatus(healthy, response.body, url)
    })

  }
}
*/
