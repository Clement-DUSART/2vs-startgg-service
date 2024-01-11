package vs.api.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class BracketSet(
    setIdentifier: String,
    phase: String,
    player1Prefix: Option[String],
    player1GamerTag: String,
    player2Prefix: Option[String],
    player2GamerTag: String,
    score: String
)

object BracketSet:
    given bracketSetCodec: Codec[BracketSet] = deriveCodec[BracketSet]
