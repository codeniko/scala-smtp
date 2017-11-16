package controllers

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._


class StatusControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  "Status Controller" should {
    "send 200 on a /status request" in {
      route(app, FakeRequest(GET, "/status")).map(status) mustBe Some(OK)
    }
  }
}
