package jaco.project.client

import cats.MonadThrow
import cats.effect.kernel.Concurrent
import io.circe.Json
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client

case class GithubClient[F[_] : MonadThrow : Concurrent](client: Client[F]) {

  def checkStatus(): F[Json] =
    client.get("https://www.githubstatus.com/api/v2/status.json") { response =>
      response.as[Json]
    }

}
