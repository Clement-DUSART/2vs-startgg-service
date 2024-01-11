package vs.api.startgg.response

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import vs.api.startgg.response.GetTournamentIdResponse.Event

case class GetTournamentIdResponse(data: Event)
object GetTournamentIdResponse {
  case class Event(event: Tournament)
  case class Tournament(tournament: TournamentContent)
  case class TournamentContent(id: Int)
  given Codec[TournamentContent] = deriveCodec[TournamentContent]
  given Codec[Tournament] = deriveCodec[Tournament]
  given Codec[Event] = deriveCodec[Event]
  given Codec[GetTournamentIdResponse] = deriveCodec[GetTournamentIdResponse]

}
