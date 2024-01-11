package vs.api.startgg.response

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import vs.api.startgg.response.GetPhaseGroupResponse.ResponseData
case class GetPhaseGroupResponse(data: ResponseData)

object GetPhaseGroupResponse:

    case class ResponseData(event: PhaseGroupArray)

    case class PhaseGroupArray(phaseGroups: Seq[PhaseGroup])
    case class PhaseGroup(id: Int, displayIdentifier: String)

    given phaseGroupCodec: Codec[PhaseGroup] = deriveCodec[PhaseGroup]
    given dataCodec: Codec[ResponseData] = deriveCodec[ResponseData]

    given phaseGroupArrayCodec: Codec[PhaseGroupArray] = deriveCodec[PhaseGroupArray]
    given phaseGroupResponseCodec: Codec[GetPhaseGroupResponse] = deriveCodec[GetPhaseGroupResponse]
