package vs.api.startgg.model

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GGParticipant(player: GGPlayer, user: GGUser)

object GGParticipant:
    given ggParticipantCodec: Codec[GGParticipant] = deriveCodec[GGParticipant]
