package vs.api.service

import cats.data.EitherT
import cats.effect.Async
import cats.implicits.*
import vs.api.model.Player
import vs.api.startgg.client.StartGGClient
import vs.api.startgg.model.GGParticipant
import vs.api.startgg.query.Query.GetPlayersFromTournamentQuery
import vs.api.startgg.query.Response.GetPlayersResponse

trait StartGGCaller[F[_]]:
    def getTournamentsParticipants(
        tournamentSlug: String,
        apiToken: String
    ): EitherT[F, String, Seq[Player]]

class StartGGCallerImpl[F[_]: Async](startGGClient: StartGGClient[F])
    extends StartGGCaller[F]:
    private def mapPlayers(ggPlayer: Seq[GGParticipant]): Seq[Player] = ggPlayer
      .map { ggPlayer =>
        Player(
          gamerTag = ggPlayer.player.gamerTag,
          prefix = ggPlayer.player.prefix,
          country = ggPlayer.user.location.country,
          city = ggPlayer.user.location.city,
          twitter = ggPlayer
            .user
            .authorizations
            .flatMap(_.find(_.`type` == "TWITTER"))
            .map(_.externalUsername),
          twitch = ggPlayer
            .user
            .authorizations
            .flatMap(_.find(_.`type` == "TWITCH"))
            .map(_.externalUsername)
        )
      }

    override def getTournamentsParticipants(
        tournamentSlug: String,
        apiToken: String
    ): EitherT[F, String, Seq[Player]] = {
      val query = new GetPlayersFromTournamentQuery(tournamentSlug)
      startGGClient
        .makePaginatedRequest[
          GetPlayersFromTournamentQuery,
          GetPlayersResponse](query, apiToken, 50)
        .map(_.data.tournament.participants.nodes)
        .map(mapPlayers)
        .leftMap(_.toString)
    }
