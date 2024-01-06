package vs.main

import cats.effect.*
import com.comcast.ip4s.host
import com.comcast.ip4s.port
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.client3.http4s.Http4sBackend
import sttp.model.Uri
import sttp.tapir.server.http4s.Http4sServerInterpreter
import vs.api.service.StartGGCallerImpl
import vs.api.startgg.client.StartGGClient

object Main extends IOApp:

    override def run(args: List[String]): IO[ExitCode] =
        val httpServer =
          for {
            backEnd <- Http4sBackend.usingDefaultEmberClientBuilder[IO]()
            client =
              new StartGGClient[IO](
                Uri("https", "api.start.gg", Seq("gql/alpha")),
                backEnd
              )
            caller = new StartGGCallerImpl[IO](client)
            controller = new Controller[IO](caller)
            port = port"9000"
            routes = Http4sServerInterpreter[IO]().toRoutes(controller.all)

            server <-
              EmberServerBuilder
                .default[IO]
                .withHost(host"0.0.0.0")
                .withPort(port)
                .withHttpApp(Router("/" -> routes).orNotFound)
                .build

          } yield server

        httpServer
          .use { server =>
            for {
              _ <- IO.println(
                s"Server started at http://localhost:${server.address.getPort}. Press ENTER key to exit."
              )
              _ <- IO.never
            } yield ()
          }
          .map(_ => ExitCode.Success)
