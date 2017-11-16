package controllers

import play.api.mvc.{Action, AbstractController, ControllerComponents}
import javax.inject.Inject
import com.google.inject.Singleton

@Singleton
class BuildInfoController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def buildInfo() = Action {
    Ok(views.html.buildinfo())
  }

}
