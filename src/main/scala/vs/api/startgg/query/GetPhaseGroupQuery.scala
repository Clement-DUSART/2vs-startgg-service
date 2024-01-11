package vs.api.startgg.query

import vs.api.startgg.query.Query.*

class GetPhaseGroupQuery(eventId: String) extends SimpleQuery(GetPhaseGroupQuery.query, Map("eventId" -> eventId))

object GetPhaseGroupQuery:
    val query: String =
      """
        | query getPhaseGroups($eventId: ID!) {
        |   event(id: $eventId) {
        |     phaseGroups {
        |       id
        |       displayIdentifier
        |     }
        |   }
        | }
        |""".stripMargin
