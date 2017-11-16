package controllers

import play.api.test.FakeRequest
import play.api.libs.json._
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.mockito.Matchers.any
import test._

import scala.concurrent.Future

import scala.language.postfixOps

class HealthCheckControllerSpec extends MockitoAppPlaySpec {

  "HealthCheckController" should {

    "respond healthy" when {
      "everything is healthy" in {

        val eventualResult = route(app, FakeRequest(GET, "/healthcheck")).get

        val json = contentAsJson(eventualResult)

        (json \ "healthy").get.as[Boolean] mustBe true
      }
    }
  }
}
