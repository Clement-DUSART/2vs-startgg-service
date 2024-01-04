package vs.api.startgg.query

import io.circe.Decoder.Result
import io.circe.Codec
import io.circe.HCursor
import io.circe.generic.semiauto.deriveCodec
import vs.api.startgg.model.GGParticipant

object Response {
  case class PageInfo(page: Int, perPage: Int, totalPages: Int)

  sealed trait PaginatedResponse[T]:
      def getResponsePageInfo: PageInfo
      def combineWithResponse(response: T): T

  case class GetPlayersResponse(data: TournamentResponse)
      extends PaginatedResponse[GetPlayersResponse]:
      override def combineWithResponse(
          response: GetPlayersResponse
      ): GetPlayersResponse = {
        val newNodes =
          data.tournament.participants.nodes ++
            response.data.tournament.participants.nodes
        val newPageInfo = response.data.tournament.participants.pageInfo
        val newNodesPlayerResponse = NodePlayerResponse(newPageInfo, newNodes)

        GetPlayersResponse(
          TournamentResponse(TournamentPlayerResponse(newNodesPlayerResponse)))
      }

      override def getResponsePageInfo: PageInfo =
        data.tournament.participants.pageInfo

  case class TournamentResponse(tournament: TournamentPlayerResponse)
  case class TournamentPlayerResponse(participants: NodePlayerResponse)
  case class NodePlayerResponse(pageInfo: PageInfo, nodes: Seq[GGParticipant])

  given nodePlayerResponseCodec: Codec[NodePlayerResponse] =
    deriveCodec[NodePlayerResponse]
  given tournamentPlayerResponseCodec: Codec[TournamentPlayerResponse] =
    deriveCodec[TournamentPlayerResponse]
  given tournamentResponseCodec: Codec[TournamentResponse] =
    deriveCodec[TournamentResponse]
  given responseCodec: Codec[GetPlayersResponse] =
    deriveCodec[GetPlayersResponse]
}
