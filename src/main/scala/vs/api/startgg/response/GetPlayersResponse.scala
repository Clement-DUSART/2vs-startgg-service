package vs.api.startgg.response

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import vs.api.startgg.model.GGParticipant
import vs.api.startgg.response.GetPlayersResponse.*
import vs.api.startgg.response.Response.*

case class GetPlayersResponse(data: TournamentResponse) extends PaginatedResponse[GetPlayersResponse]:
    override def combineWithResponse(response: GetPlayersResponse): GetPlayersResponse = {
      val newNodes = data.tournament.participants.nodes ++ response.data.tournament.participants.nodes
      val newPageInfo = response.data.tournament.participants.pageInfo
      val newNodesPlayerResponse = NodePlayerResponse(newPageInfo, newNodes)

      GetPlayersResponse(TournamentResponse(TournamentPlayerResponse(newNodesPlayerResponse)))
    }

    override def getResponsePageInfo: PageInfo = data.tournament.participants.pageInfo

object GetPlayersResponse:
    case class TournamentResponse(tournament: TournamentPlayerResponse)

    case class TournamentPlayerResponse(participants: NodePlayerResponse)

    case class NodePlayerResponse(pageInfo: PageInfo, nodes: Seq[GGParticipant])

    given nodePlayerResponseCodec: Codec[NodePlayerResponse] = deriveCodec[NodePlayerResponse]

    given tournamentPlayerResponseCodec: Codec[TournamentPlayerResponse] = deriveCodec[TournamentPlayerResponse]

    given tournamentResponseCodec: Codec[TournamentResponse] = deriveCodec[TournamentResponse]

    given responseCodec: Codec[GetPlayersResponse] = deriveCodec[GetPlayersResponse]
