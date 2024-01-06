package vs.api.startgg.client

import cats.Applicative
import cats.data.EitherT
import cats.effect.Async
import cats.effect.Concurrent
import cats.effect.kernel.Outcome.Canceled
import cats.effect.kernel.Outcome.Errored
import cats.effect.kernel.Outcome.Succeeded
import cats.implicits.*
import io.circe
import io.circe.Decoder
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3.*
import sttp.client3.circe.asJson
import sttp.client3.circe.circeBodySerializer
import sttp.model.Uri
import vs.api.startgg.query.Query.PaginatedQuery
import vs.api.startgg.query.Query.Pagination
import vs.api.startgg.query.Query.SimpleQuery
import vs.api.startgg.response.Response.PaginatedResponse

class StartGGClient[F[_]: Async: Applicative](
    startGGApiUri: Uri,
    httpClient: SttpBackend[F, Fs2Streams[F]]):
    private def concurrentRequests[R: Decoder](
        requests: List[SimpleQuery],
        apiToken: String) = EitherT(
      for {
        fibers <- requests
          .map(request => makeRequest[R](request, apiToken).value)
          .traverse(Concurrent[F].start)

        outcomes <- fibers.traverse(_.join)
        results <- outcomes
          .traverse {
            case Succeeded(fa) =>
              fa
            case Errored(e) =>
              Left[Any, R](e).pure[F]
            case Canceled() =>
              Left[Any, R](Exception("Cancelled request")).pure[F]
          }
          .map(_.sequence)
      } yield results
    )

    def makeRequest[R: Decoder](
        simpleQuery: SimpleQuery,
        apiToken: String): EitherT[F, Any, R] = EitherT(
      basicRequest
        .post(startGGApiUri)
        .body(simpleQuery)
        .headers(Map("Authorization" -> s"Bearer $apiToken"))
        .response(asJson[R])
        .send(httpClient)
        .map(_.body)
    )

    def makePaginatedRequest[R <: PaginatedResponse[R]: Decoder](
        query: PaginatedQuery,
        apiToken: String,
        perPage: Int = 50): EitherT[F, Any, R] =
        val firstQuery = query.withPaginationInfo(Pagination(1, perPage))

        for {
          firstResponse <- makeRequest(firstQuery, apiToken)
          pageInfo = firstResponse.getResponsePageInfo
          nextQueries =
            Range
              .inclusive(2, pageInfo.totalPages, 1)
              .map(page => Pagination(page, perPage))
              .map(query.withPaginationInfo)
              .toList

          subSequentResponse <- concurrentRequests(nextQueries, apiToken)
        } yield subSequentResponse
          .fold(firstResponse)((a, b) => a.combineWithResponse(b))
