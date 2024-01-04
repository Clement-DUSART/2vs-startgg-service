package vs.api.startgg.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GGLocation(country: Option[String], city: Option[String])

object GGLocation:
    given ggLocationCodec: Codec[GGLocation] = deriveCodec[GGLocation]
