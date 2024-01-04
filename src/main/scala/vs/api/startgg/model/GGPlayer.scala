package vs.api.startgg.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GGPlayer(gamerTag: String, prefix: Option[String])

object GGPlayer:
    given ggPlayerCodec: Codec[GGPlayer] = deriveCodec[GGPlayer]
