package vs.api.startgg.response

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import vs.api.startgg.response.GetEventsResponse.*
import vs.api.startgg.response.Response.PageInfo
import vs.api.startgg.response.Response.PaginatedResponse

case class GetEventsResponse(data: EventData) extends PaginatedResponse[GetEventsResponse] {
  override def getResponsePageInfo: PageInfo = data.event.sets.pageInfo

  override def combineWithResponse(response: GetEventsResponse): GetEventsResponse = {
    val newNodes = data.event.sets.nodes ++ response.data.event.sets.nodes

    GetEventsResponse(EventData(EventNode(Sets(response.getResponsePageInfo, newNodes))))
  }
}

object GetEventsResponse:
    case class EventData(event: EventNode)
    case class EventNode(sets: Sets)
    case class Sets(pageInfo: PageInfo, nodes: Seq[Node])
    case class Node(identifier: String, fullRoundText: String, slots: Seq[Slot])
    case class Slot(standing: SlotStanding)
    case class SlotStanding(stats: Stat, entrant: Entrant)

    case class Stat(score: Score)
    case class Score(value: Int)
    case class Entrant(standing: Standing)
    case class Standing(player: Player)
    case class Player(prefix: Option[String], gamerTag: String)

    given getEventsResponse: Codec[GetEventsResponse] = deriveCodec[GetEventsResponse]
    given eventDataCodec: Codec[EventData] = deriveCodec[EventData]
    given eventNodeCodec: Codec[EventNode] = deriveCodec[EventNode]
    given setsCodec: Codec[Sets] = deriveCodec[Sets]
    given nodeCodec: Codec[Node] = deriveCodec[Node]
    given slotCodec: Codec[Slot] = deriveCodec[Slot]

    given statCodec: Codec[Stat] = deriveCodec[Stat]
    given scoreCodec: Codec[Score] = deriveCodec[Score]
    given slotStandingCodec: Codec[SlotStanding] = deriveCodec[SlotStanding]
    given entrantCodec: Codec[Entrant] = deriveCodec[Entrant]
    given standingCodec: Codec[Standing] = deriveCodec[Standing]
    given playerCodec: Codec[Player] = deriveCodec[Player]
