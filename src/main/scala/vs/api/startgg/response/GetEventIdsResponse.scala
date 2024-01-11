package vs.api.startgg.response

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import vs.api.startgg.response.GetEventIdsResponse.GetEventIdsResponseTournament

case class GetEventIdsResponse(data: GetEventIdsResponseTournament)

object GetEventIdsResponse {

  case class GetEventIdsResponseTournament(tournament: GetEventIdsResponseEvents)
  case class GetEventIdsResponseEvents(events: Seq[GetEventIdsResponseData])
  case class GetEventIdsResponseData(id: Int, name: String)

  given Codec[GetEventIdsResponseTournament] = deriveCodec[GetEventIdsResponseTournament]
  given Codec[GetEventIdsResponseEvents] = deriveCodec[GetEventIdsResponseEvents]
  given Codec[GetEventIdsResponseData] = deriveCodec[GetEventIdsResponseData]
  given Codec[GetEventIdsResponse] = deriveCodec[GetEventIdsResponse]

}
