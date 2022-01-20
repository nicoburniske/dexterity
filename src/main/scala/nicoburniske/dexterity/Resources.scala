package nicoburniske.dexterity

import cats.effect.IO
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

object Resources {
  val client = asyncHttpClient()
  val backend = AsyncHttpClientCatsBackend.usingClient[IO](client)
}
