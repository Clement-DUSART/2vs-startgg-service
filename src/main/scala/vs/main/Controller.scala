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
import vs.api.model.Player
import vs.api.service.StartGGCaller

class Controller[F[_]: Async: Functor](startGGCaller: StartGGCaller[F]):
    implicit val playerSchema: Schema[Player] = Schema.derived[Player]

    val getPlayersEndPoint = endpoint
      .get
      .in("players")
      .in(query[String]("tournament"))
      .in(auth.bearer[String]())
      .out(jsonBody[Seq[Player]])

    val getPlayersServerEndPoint
        : Full[Unit, Unit, (String, String), Unit, Seq[Player], Any, F] =
      getPlayersEndPoint.serverLogic[F](tuple =>
        startGGCaller
          .getTournamentsParticipants(tuple._1, tuple._2)
          .leftMap(_ => ())
          .value)

    val apiEndpoints: List[ServerEndpoint[Any, F]] = List(
      getPlayersServerEndPoint)

    val all: List[ServerEndpoint[Any, F]] = apiEndpoints
