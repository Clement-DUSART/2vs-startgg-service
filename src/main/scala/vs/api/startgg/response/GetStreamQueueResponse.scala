package vs.api.startgg.response

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import vs.api.startgg.response.GetStreamQueueResponse.Data

case class GetStreamQueueResponse(data: Data)
object GetStreamQueueResponse {

  case class Player(gamerTag: String)
  case class Standing(player: Player)
  case class Entrant(standing: Standing)
  case class SlotStanding(entrant: Entrant)
  case class Slot(standing: SlotStanding)
  case class PhaseGroup(displayIdentifier: String)
  case class Set(identifier: String, fullRoundText: String, phaseGroup: PhaseGroup, slots: Seq[Slot])

  case class Stream(streamName: String)
  case class StreamQueue(stream: Stream, sets: Seq[Set])

  case class Data(streamQueue: Seq[StreamQueue])

  given Codec[Player] = deriveCodec[Player]
  given Codec[Standing] = deriveCodec[Standing]
  given Codec[Entrant] = deriveCodec[Entrant]
  given Codec[SlotStanding] = deriveCodec[SlotStanding]
  given Codec[Slot] = deriveCodec[Slot]
  given Codec[PhaseGroup] = deriveCodec[PhaseGroup]
  given Codec[Set] = deriveCodec[Set]
  given Codec[Stream] = deriveCodec[Stream]
  given Codec[StreamQueue] = deriveCodec[StreamQueue]
  given Codec[Data] = deriveCodec[Data]
  given Codec[GetStreamQueueResponse] = deriveCodec[GetStreamQueueResponse]

}
