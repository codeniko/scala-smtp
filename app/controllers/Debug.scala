package controllers

import akka.util.ByteString
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.typesafe.config.ConfigRenderOptions
import play.api.Configuration
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.mvc.{Action, AbstractController, ControllerComponents}
import play.api.http.HttpEntity

import scala.collection.JavaConverters._
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class DebugController @Inject()(cfg: Configuration, cc: ControllerComponents)(implicit @Named("mdcEC") ec: ExecutionContext) extends AbstractController(cc) {

  def config() = Action {
    val s = cfg.underlying.root().render(ConfigRenderOptions.concise())

    Ok.sendEntity(HttpEntity.Strict(ByteString(s.getBytes), Some("application/json")))
  }

}
