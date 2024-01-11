package vs.api.startgg.query

import vs.api.startgg.query.Query.SimpleQuery

class GetStreamQueueRequest(tournamentId: String)
    extends SimpleQuery(GetStreamQueueRequest.query, Map("tournamentId" -> tournamentId))
object GetStreamQueueRequest {

  val query =
    s"""
       | query getStreamQueue($$tournamentId: ID!) {
       |   streamQueue(tournamentId: $$tournamentId) {
       |     stream {
       |       streamName
       |     }
       |     sets {
       |       identifier
       |       fullRoundText
       |       phaseGroup {
       |         displayIdentifier
       |       }
       |       slots {
       |         standing {
       |           entrant {
       |             standing {
       |               player {
       |                 prefix
       |                 gamerTag
       |               }
       |             }
       |           }
       |         }
       |       }
       |     }
       |   }
       | }
       |""".stripMargin

}
