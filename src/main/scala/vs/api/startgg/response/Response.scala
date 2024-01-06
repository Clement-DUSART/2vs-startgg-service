package vs.api.startgg.response

import io.circe.Codec
import io.circe.HCursor

object Response {
  case class PageInfo(page: Int, perPage: Int, totalPages: Int)

  trait PaginatedResponse[T]:
      def getResponsePageInfo: PageInfo
      def combineWithResponse(response: T): T
}
