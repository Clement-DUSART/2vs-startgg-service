package vs.api.startgg.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GGAuthorization(`type`: String, externalUsername: Option[String])

object GGAuthorization:
    given ggAuthorizationCodec: Codec[GGAuthorization] =
      deriveCodec[GGAuthorization]
