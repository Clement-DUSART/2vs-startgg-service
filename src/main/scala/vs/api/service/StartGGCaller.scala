package vs.api.service

import cats.data.EitherT
import cats.effect.Async
import cats.implicits.*
import vs.api.model.BracketSet
import vs.api.model.Player
import vs.api.startgg.client.StartGGClient
import vs.api.startgg.model.GGParticipant
import vs.api.startgg.query.GetEventsQuery
import vs.api.startgg.query.GetPhaseGroupQuery
import vs.api.startgg.query.GetPlayersFromTournamentQuery
import vs.api.startgg.response.GetEventsResponse
import vs.api.startgg.response.GetPhaseGroupResponse
import vs.api.startgg.response.GetPlayersResponse

trait StartGGCaller[F[_]]:
    def getTournamentsParticipants(
        tournamentSlug: String,
        apiToken: String): EitherT[F, String, Seq[Player]]

    def getEventBracket(
        eventId: String,
        phaseGroupIdentifier: String,
        apiToken: String): EitherT[F, String, Seq[BracketSet]]

class StartGGCallerImpl[F[_]: Async](startGGClient: StartGGClient[F])
    extends StartGGCaller[F]:
    private def mapPlayers(ggPlayer: Seq[GGParticipant]): Seq[Player] = ggPlayer
      .map { ggPlayer =>
        Player(
          gamerTag = ggPlayer.player.gamerTag,
          prefix = ggPlayer.player.prefix,
          country = ggPlayer.user.flatMap(_.location.country),
          city = ggPlayer.user.flatMap(_.location.city),
          twitter = ggPlayer
            .user
            .flatMap(_.authorizations)
            .getOrElse(List.empty)
            .find(_.`type` == "TWITTER")
            .flatMap(_.externalUsername),
          twitch = ggPlayer
            .user
            .flatMap(_.authorizations)
            .getOrElse(List.empty)
            .find(_.`type` == "TWITCH")
            .flatMap(_.externalUsername)
        )
      }

    private def mapEventsResponse(response: GetEventsResponse) = response
      .data
      .event
      .sets
      .nodes
      .map(event =>
        BracketSet(
          setIdentifier = event.identifier,
          phase = event.fullRoundText,
          player1Prefix =
            event.slots.head.standing.entrant.standing.player.prefix,
          player1GamerTag =
            event.slots.head.standing.entrant.standing.player.gamerTag,
          player2Prefix =
            event.slots.last.standing.entrant.standing.player.prefix,
          player2GamerTag =
            event.slots.last.standing.entrant.standing.player.gamerTag,
          score =
            s"${event.slots.head.standing.stats.score.value} - ${event.slots.last.standing.stats.score.value}"
        )
      )

    private def bracketSetSorter(b1: BracketSet, b2: BracketSet): Boolean =
      if (b1.setIdentifier.length == b2.setIdentifier.length)
        b1.setIdentifier.compareTo(b2.setIdentifier) < 0
      else
        b1.setIdentifier.length - b2.setIdentifier.length < 0

    override def getTournamentsParticipants(
        tournamentSlug: String,
        apiToken: String): EitherT[F, String, Seq[Player]] = {
      val query = new GetPlayersFromTournamentQuery(tournamentSlug)
      startGGClient
        .makePaginatedRequest[GetPlayersResponse](query, apiToken, 75)
        .map(_.data.tournament.participants.nodes)
        .map(mapPlayers)
        .leftMap(_.toString)
    }

    override def getEventBracket(
        eventId: String,
        phaseGroupIdentifier: String,
        apiToken: String): EitherT[F, String, Seq[BracketSet]] =
      for {
        phaseGroupIdOpt <- getPhaseGroupId(
          phaseGroupIdentifier,
          eventId,
          apiToken
        ).leftMap(_.toString)
        phaseGroupId <- EitherT
          .fromEither[F](phaseGroupIdOpt.toRight("Phase group id not found"))
        events <- startGGClient
          .makePaginatedRequest[GetEventsResponse](
            new GetEventsQuery(eventId, phaseGroupId.toString),
            apiToken,
            perPage = 40
          )
          .leftMap(_.toString)
      } yield mapEventsResponse(events)
        .sortWith((a, b) => bracketSetSorter(a, b))

    private def getPhaseGroupId(
        groupIdentifier: String,
        eventId: String,
        apiToken: String): EitherT[F, Any, Option[Int]] = {
      val query = new GetPhaseGroupQuery(eventId)
      for {
        groups <- startGGClient
          .makeRequest[GetPhaseGroupResponse](query, apiToken)
        groupPhaseId = groups
          .data
          .event
          .phaseGroups
          .find(_.displayIdentifier == groupIdentifier)
          .map(_.id)
      } yield groupPhaseId
    }
