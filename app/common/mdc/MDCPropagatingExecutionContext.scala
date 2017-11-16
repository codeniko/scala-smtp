package common.mdc

import org.slf4j.MDC
import scala.concurrent.ExecutionContext
import javax.inject.Inject

// http://code.hootsuite.com/logging-contextual-info-in-an-asynchronous-scala-application/

/**
 * Wrapper around an existing ExecutionContext that propagates MDC information.
 */
class MDCPropagatingExecutionContext @Inject()(wrapped: ExecutionContext) extends ExecutionContext {

  self =>

  override def execute(r: Runnable): Unit = wrapped.execute(r)

  override def reportFailure(t: Throwable): Unit = wrapped.reportFailure(t)

  override def prepare(): ExecutionContext = new ExecutionContext {
    // Save the call-site MDC state
    val context = Option(MDC.getCopyOfContextMap)

    def execute(r: Runnable) {
      self.execute(new Runnable {
        def run(): Unit = {
          // Save the existing execution-site MDC state
          val oldContext = Option(MDC.getCopyOfContextMap)
          try {
            // Set the call-site MDC state into the execution-site MDC
            if (context.nonEmpty)
              MDC.setContextMap(context.getOrElse(new java.util.HashMap[String, String]()))
            else
              MDC.clear()

            r.run()
          } finally {
            // Restore the existing execution-site MDC state
            if (oldContext.nonEmpty)
              MDC.setContextMap(oldContext.getOrElse(new java.util.HashMap[String, String]()))
            else
              MDC.clear()
          }
        }
      })
    }

    def reportFailure(t: Throwable):Unit = self.reportFailure(t)

  }
}

object MDCPropagatingExecutionContext {
  def apply(wrapped: ExecutionContext): MDCPropagatingExecutionContext = {
    new MDCPropagatingExecutionContext(wrapped)
  }
}

object AttachExecutionContext {
  def apply(mdc: Option[java.util.Map[String, String]]) {
    if (mdc.nonEmpty)
      MDC.setContextMap(mdc.getOrElse(new java.util.HashMap[String, String]()))
    else
      MDC.clear()
  }
}
