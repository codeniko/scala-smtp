package filters

import javax.inject.Inject

import akka.stream.Materializer
import com.google.inject.Singleton
import log.ClassLogger
import log.Utils.statusClass
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class I13nFilter @Inject()(
  implicit val mat: Materializer,
  ec: ExecutionContext
) extends Filter with ClassLogger {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = (endTime - startTime).toString

      logger.debug(s"requestTime=${requestTime}")
      result.withHeaders("Request-Time" → requestTime)
    }
  }
}
