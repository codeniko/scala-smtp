package enhancements

import scala.concurrent.duration.FiniteDuration
import akka.actor.ActorSystem

import scala.concurrent.{ExecutionContext, Future, Promise, TimeoutException}

// Implements futures with timeouts, based on the approach taken here:
// http://stackoverflow.com/questions/16304471/scala-futures-built-in-timeout/16305056#16305056

object FutureWithTimeout {
  final val system = ActorSystem.create()

  implicit class FutureWithTimeoutPimp[T](f: Future[T]) {

    def withTimeout(afterDuration: FiniteDuration)(implicit ec: ExecutionContext) = {
      val delayed = akka.pattern.after(afterDuration, using = system.scheduler)(Future.failed(
          new TimeoutException("Operation timed out after " + afterDuration.toMillis.toString + " millis")
      ))
      val combinedFut = Future.firstCompletedOf(List(f, delayed))
      combinedFut
    }
  }
}
