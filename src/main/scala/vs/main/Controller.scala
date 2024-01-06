package vs.main

import cats.Functor
import cats.effect.kernel.Async
import io.circe.*
import sttp.tapir.*
import sttp.tapir.Schema.SName
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint.Full
import vs.api.model.BracketSet
import vs.api.model.Player
import vs.api.service.StartGGCaller

class Controller[F[_]: Async: Functor](startGGCaller: StartGGCaller[F]):
    given playerSchema: Schema[Player] = Schema.derived[Player]
    given bracketSetSchema: Schema[BracketSet] = Schema.derived[BracketSet]

    val getPlayersEndPoint
        : Endpoint[Unit, (String, String), String, Seq[Player], Any] = endpoint
      .get
      .in("players")
      .in(query[String]("tournament"))
      .in(auth.bearer[String]())
      .out(jsonBody[Seq[Player]])
      .errorOut(stringBody)

    val getEventsEndPoint: Endpoint[Unit, (String, String, String), String, Seq[
      BracketSet
    ], Any] = endpoint
      .get
      .in("events")
      .in(query[String]("eventId"))
      .in(query[String]("groupIdentifier"))
      .in(auth.bearer[String]())
      .out(jsonBody[Seq[BracketSet]])
      .errorOut(stringBody)

    val getPlayersServerEndPoint
        : Full[Unit, Unit, (String, String), String, Seq[Player], Any, F] =
      getPlayersEndPoint.serverLogic[F](tuple =>
        startGGCaller.getTournamentsParticipants(tuple._1, tuple._2).value
      )

    val getEventsServerEndPoint
        : Full[Unit, Unit, (String, String, String), String, Seq[
          BracketSet
        ], Any, F] = getEventsEndPoint.serverLogic[F](tuple =>
      startGGCaller.getEventBracket(tuple._1, tuple._2, tuple._3).value
    )

    val apiEndpoints: List[ServerEndpoint[Any, F]] = List(
      getPlayersServerEndPoint,
      getEventsServerEndPoint
    )

    val all: List[ServerEndpoint[Any, F]] = apiEndpoints
