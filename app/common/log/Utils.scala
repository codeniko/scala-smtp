package log


object Utils extends ClassLogger {

  def statusClass(status : Int) : String =
    if (100 to 200 contains status) "2xx"
    else if (200 to 300 contains status) "2xx"
    else if (300 to 400 contains status) "3xx"
    else if (400 to 500 contains status) "4xx"
    else if (500 to 600 contains status) "5xx"
    else {
      logger.error(s"Invalid status code=$status")

      "other"
    }
}
