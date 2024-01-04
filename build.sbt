val scala3Version = "3.3.1"

val http4sVersion = "0.23.24"
val tapirVersion = "1.9.6"
val sttpVersion = "3.9.1"
val circeVersion = "0.14.5"

val http4sDep = Seq(
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion
)

val tapirDep = Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "org.http4s" %% "http4s-ember-server" % "0.23.24"
)

val sttpDep = Seq(
  "com.softwaremill.sttp.client3" %% "http4s-backend" % sttpVersion,
  "com.softwaremill.sttp.client3" %% "circe" % sttpVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.9.6"
)

val circeDep = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion
)

lazy val root = project
    .in(file("."))
    .settings(
      name := "2vs-startgg-service",
      version := "0.1.0-SNAPSHOT",
      libraryDependencies ++= http4sDep ++ tapirDep ++ sttpDep ++ circeDep,
      scalaVersion := scala3Version
    )

assembly / assemblyJarName := "2vs-startgg-service.jar"
