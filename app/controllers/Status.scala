package controllers

import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import play.api.Configuration
import play.api.mvc.{Action, AbstractController, ControllerComponents}
import play.api.http.FileMimeTypes
import scala.concurrent.ExecutionContext


@Singleton
class StatusController @Inject() (cfg: Configuration, cc: ControllerComponents)(implicit @Named("mdcEC") ec: ExecutionContext) extends AbstractController(cc) {
  def status = Action {
    Ok("ok")
  }
}
