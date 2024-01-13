package vs.api.service

import cats.data.EitherT
import cats.effect.Async
import cats.implicits.*
import vs.api.model.BracketSet
import vs.api.model.Event
import vs.api.model.Player
import vs.api.model.StreamQueue
import vs.api.startgg.client.StartGGClient
import vs.api.startgg.model.GGParticipant
import vs.api.startgg.query.*
import vs.api.startgg.response.*
import vs.api.startgg.response.GetEventsResponse.SlotStanding

trait StartGGCaller[F[_]] {
  def getTournamentsParticipants(tournamentSlug: String, apiToken: String): EitherT[F, String, Seq[Player]]

  def getTournamentEvents(tournamentSlug: String, apiToken: String): EitherT[F, String, Seq[Event]]

  def getEventBracket(
      eventId: String,
      phaseGroupIdentifier: String,
      apiToken: String
  ): EitherT[F, String, Seq[BracketSet]]

  def getStreamQueue(eventId: String, streamName: String, apiToken: String): EitherT[F, String, Seq[StreamQueue]]
}

class StartGGCallerImpl[F[_]: Async](startGGClient: StartGGClient[F]) extends StartGGCaller[F] {
  private def mapPlayers(ggPlayer: Seq[GGParticipant]): Seq[Player] = ggPlayer.map { ggPlayer =>
    Player(
      gamerTag = ggPlayer.player.gamerTag,
      prefix = ggPlayer.player.prefix,
      country = ggPlayer.user.flatMap(_.location).flatMap(_.country),
      city = ggPlayer.user.flatMap(_.location).flatMap(_.city),
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

  private def getBracketSetFromStanding(
      identifier: String,
      phase: String,
      player1Opt: Option[SlotStanding],
      player2Opt: Option[SlotStanding]
  ): Option[BracketSet] =
    for {
      player1 <- player1Opt
      player2 <- player2Opt
    } yield BracketSet(
      setIdentifier = identifier,
      phase = phase,
      player1Prefix = player1.entrant.standing.player.prefix,
      player1GamerTag = player1.entrant.standing.player.gamerTag,
      player2Prefix = player2.entrant.standing.player.prefix,
      player2GamerTag = player2.entrant.standing.player.gamerTag,
      score = s"${player1.stats.score.value.getOrElse(0)} - ${player2.stats.score.value.getOrElse(0)}"
    )

  private def mapEventsResponse(response: GetEventsResponse) = response
    .data
    .event
    .sets
    .nodes
    .flatMap(event =>
      getBracketSetFromStanding(
        event.identifier,
        event.fullRoundText,
        event.slots.headOption.flatMap(_.standing),
        event.slots.lastOption.flatMap(_.standing)))

  private def bracketSetSorter(b1: BracketSet, b2: BracketSet): Boolean =
    if (b1.setIdentifier.length == b2.setIdentifier.length)
      b1.setIdentifier.compareTo(b2.setIdentifier) < 0
    else
      b1.setIdentifier.length - b2.setIdentifier.length < 0

  override def getTournamentsParticipants(tournamentSlug: String, apiToken: String): EitherT[F, String, Seq[Player]] = {
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
      apiToken: String
  ): EitherT[F, String, Seq[BracketSet]] =
    for {
      phaseGroupIdOpt <- getPhaseGroupId(phaseGroupIdentifier, eventId, apiToken).leftMap(_.toString)
      phaseGroupId <- EitherT.fromEither[F](phaseGroupIdOpt.toRight("Phase group id not found"))
      events <- startGGClient
        .makePaginatedRequest[GetEventsResponse](
          new GetEventsQuery(eventId, phaseGroupId.toString),
          apiToken,
          perPage = 40)
        .leftMap(_.toString)
    } yield mapEventsResponse(events).sortWith((a, b) => bracketSetSorter(a, b))

  override def getTournamentEvents(tournamentSlug: String, apiToken: String): EitherT[F, String, Seq[Event]] = {
    val query = new GetEventIdsQuery(tournamentSlug)
    startGGClient
      .makeRequest[GetEventIdsResponse](query, apiToken)
      .map { ggResponse =>
        ggResponse
          .data
          .tournament
          .events
          .map { ggEvent =>
            Event(ggEvent.id.toString, ggEvent.name)
          }
      }
      .leftMap(_.toString)
  }

  private def getPhaseGroupId(
      groupIdentifier: String,
      eventId: String,
      apiToken: String
  ): EitherT[F, Any, Option[Int]] = {
    val query = new GetPhaseGroupQuery(eventId)
    for {
      groups <- startGGClient.makeRequest[GetPhaseGroupResponse](query, apiToken)
      groupPhaseId = groups.data.event.phaseGroups.find(_.displayIdentifier == groupIdentifier).map(_.id)
    } yield groupPhaseId
  }

  private def mapStreamQueueResponse(response: GetStreamQueueResponse): Seq[StreamQueue] = response
    .data
    .streamQueue
    .flatMap { sq =>
      sq.sets
        .map { set =>
          StreamQueue(
            identifier = set.identifier,
            pool = set.phaseGroup.displayIdentifier,
            phase = set.fullRoundText,
            player1 = set.slots.head.standing.entrant.standing.player.gamerTag,
            player2 = set.slots.last.standing.entrant.standing.player.gamerTag,
            streamName = sq.stream.streamName
          )
        }
    }
  override def getStreamQueue(
      eventId: String,
      streamName: String,
      apiToken: String
  ): EitherT[F, String, Seq[StreamQueue]] =
    for {
      tournamentIdResponse <- startGGClient
        .makeRequest[GetTournamentIdResponse](new GetTournamentIdRequest(eventId), apiToken)
        .leftMap(_.toString)
      tournamentId = tournamentIdResponse.data.event.tournament.id
      streamQueueResponse <- startGGClient
        .makeRequest[GetStreamQueueResponse](new GetStreamQueueRequest(tournamentId.toString), apiToken)
        .leftMap(_.toString)
      response = mapStreamQueueResponse(streamQueueResponse).filter(_.streamName.toUpperCase == streamName.toUpperCase)
    } yield response
}
