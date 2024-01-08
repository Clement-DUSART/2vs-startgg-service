package vs.api.startgg.query

import vs.api.startgg.query.Query.SimpleQuery

class GetEventIdsQuery(tournamentSlug: String)
    extends SimpleQuery(
      GetEventIdsQuery.query,
      Map("tournamentSlug" -> tournamentSlug)
    )

object GetEventIdsQuery {
  val query =
    s"""
       | query getEventIdsQuery($$tournamentSlug: String) {
       |  tournament(slug: $$tournamentSlug) {
       |    events {
       |      id
       |      name
       |    }
       |  }
       |}
       |""".stripMargin
}
