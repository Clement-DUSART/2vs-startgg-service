package vs.api.startgg.query

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

object Query:
    case class Pagination(page: Int, perPage: Int)

    case class SimpleQuery(query: String, variables: Map[String, String])
    given simpleQueryCodec: Codec[SimpleQuery] = deriveCodec[SimpleQuery]

    trait PaginatedQuery:
        def withPaginationInfo(pagination: Pagination): SimpleQuery
