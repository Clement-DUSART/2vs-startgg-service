package vs.api.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Player(
    gamerTag: String,
    prefix: Option[String],
    country: Option[String],
    city: Option[String],
    twitter: Option[String],
    twitch: Option[String])

object Player:
    given playerCodec: Codec[Player] = deriveCodec[Player]
