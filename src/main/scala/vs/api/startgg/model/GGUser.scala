package vs.api.startgg.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GGUser(
    authorizations: Option[List[GGAuthorization]],
    location: GGLocation
)

object GGUser:
    given ggUserCodec: Codec[GGUser] = deriveCodec[GGUser]
