package vs.api.startgg.query

import vs.api.startgg.query.Query.SimpleQuery

class GetTournamentIdRequest(tournamentSlug: String)
    extends SimpleQuery(
      GetTournamentIdRequest.query,
      Map("eventId" -> tournamentSlug)
    )

object GetTournamentIdRequest {

  val query =
    s"""
       | query getTournamentId($$eventId: ID!) {
       |  event(id: $$eventId) {
       |    tournament {
       |      id
       |    }
       |  }
       | }
       |""".stripMargin
}
