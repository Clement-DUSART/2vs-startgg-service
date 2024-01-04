package vs.api.startgg.query

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

object Query:
    case class Pagination(page: Int, perPage: Int)

    case class SimpleQuery(query: String, variables: Map[String, String])
    given simpleQueryCodec: Codec[SimpleQuery] = deriveCodec[SimpleQuery]

    sealed trait PaginatedQuery:
        def withPaginationInfo(pagination: Pagination): SimpleQuery

    class GetPlayersFromTournamentQuery(tournamentSlug: String)
        extends PaginatedQuery:
        override def withPaginationInfo(pagination: Pagination): SimpleQuery =
          SimpleQuery(
            s"""| query tournamentParticipants($$tournamentSlug: String!) {
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
                |""".stripMargin,
            Map("tournamentSlug" -> tournamentSlug)
          )
