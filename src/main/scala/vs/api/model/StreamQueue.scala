package vs.api.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class StreamQueue(streamName: String, identifier: String, pool: String, player1: String, player2: String)

object StreamQueue:
    given Codec[StreamQueue] = deriveCodec[StreamQueue]
