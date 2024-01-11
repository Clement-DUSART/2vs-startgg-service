package vs.api.startgg.query

import vs.api.startgg.query.Query.*

class GetEventsQuery(eventId: String, phaseGroupId: String) extends PaginatedQuery:
    override def withPaginationInfo(pagination: Pagination): SimpleQuery = SimpleQuery(
      GetEventsQuery.query(pagination),
      Map("eventId" -> eventId, "phaseGroupId" -> phaseGroupId))

object GetEventsQuery:
    val query =
      (pagination: Pagination) => s"""
                                     |  query EventSets($$eventId: ID!, $$phaseGroupId: ID!) {
                                     |  event(id: $$eventId) {
                                     |    sets(
                                     |      page: ${pagination.page}
                                     |      perPage: ${pagination.perPage}
                                     |      filters: {phaseGroupIds: [$$phaseGroupId]}
                                     |    ) {
                                     |      pageInfo {
                                     |        page
                                     |        perPage
                                     |        totalPages
                                     |      }
                                     |      nodes {
                                     |        identifier
                                     |        fullRoundText
                                     |        slots {
                                     |          standing {
                                     |            entrant {
                                     |              standing {
                                     |                player {
                                     |                  prefix
                                     |                  gamerTag
                                     |                }
                                     |              }
                                     |            }
                                     |            stats {
                                     |              score {
                                     |                value
                                     |              }
                                     |            }
                                     |          }
                                     |        }
                                     |      }
                                     |    }
                                     |  }
                                     |}
                                     |""".stripMargin
