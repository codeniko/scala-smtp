package controllers

import play.api.test.FakeRequest
import play.api.test.Helpers._
import test._


class BuildInfoControllerSpec extends MockitoAppPlaySpec {

  "BuildInfo Controller" should {
    "render send 200" in {
      route(app, FakeRequest(GET, "/build")).map(status) mustBe Some(OK)
    }
  }
}
