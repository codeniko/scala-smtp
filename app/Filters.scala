import javax.inject._

import filters.I13nFilter
import play.api._
import play.api.http.HttpFilters
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

@Singleton
class Filters @Inject() (env: Environment, loggingFilter: I13nFilter, corsFilter: CORSFilter) extends HttpFilters {

  override val filters = Seq(loggingFilter, corsFilter)

}
