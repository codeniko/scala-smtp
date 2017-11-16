package log

import org.slf4j.LoggerFactory
import play.api.{Logger, MarkerContext}

trait ClassLogger {
  protected val logger = new Logger(LoggerFactory.getLogger("application." + getClass.getName.stripSuffix("$"))) {

    override def error(message: => String, t: => Throwable)(implicit mc: MarkerContext) {
      super.error(s"$message exception=${t.getClass.getName}", t)
    }
  }
}
