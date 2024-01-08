package vs.api.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Event(id: String, name: String)

object Event {
  given Codec[Event] = deriveCodec[Event]
}
