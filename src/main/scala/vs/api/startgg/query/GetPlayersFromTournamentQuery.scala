package vs.api.startgg.query

import vs.api.startgg.query.Query.*

class GetPlayersFromTournamentQuery(tournamentSlug: String) extends PaginatedQuery:
    override def withPaginationInfo(pagination: Pagination): SimpleQuery = SimpleQuery(
      GetPlayersFromTournamentQuery.query(pagination),
      Map("tournamentSlug" -> tournamentSlug)
    )

object GetPlayersFromTournamentQuery:
    val query =
      (pagination: Pagination) => s"""| query tournamentParticipants($$tournamentSlug: String!) {
                                      |  tournament(slug: $$tournamentSlug) {
                                      |      participants(query: {
                                      |      page: ${pagination.page}
                                      |      perPage: ${pagination.perPage}
                                      |    }) {
                                      |      pageInfo {
                                      |        page
                                      |        totalPages
                                      |        perPage
                                      |      }
                                      |      nodes {
                                      |        player {
                                      |          prefix
                                      |          gamerTag
                                      |        }
                                      |        user {
                                      |          authorizations {
                                      |            type
                                      |            externalUsername
                                      |          }
                                      |          location {
                                      |            country
                                      |            city
                                      |          }
                                      |        }
                                      |      }
                                      |    }
                                      |  }
                                      |}
                                      |""".stripMargin
