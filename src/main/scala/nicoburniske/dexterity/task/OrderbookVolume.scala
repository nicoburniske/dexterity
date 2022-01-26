package nicoburniske.dexterity.task
import java.time.Instant

import caliban.client.CalibanClientError
import caliban.client.laminext._
import com.raquo.airstream.core.EventStream
import nicoburniske.dexterity.exchange.{DEX, SwapDetail}

import scala.collection.mutable

object OrderbookVolume {
  val SUSHI_SWAP_AVAX = "https://api.thegraph.com/subgraphs/name/sushiswap/avalanche-exchange"

  def pairSwapsPaginated(
      pair: String,
      since: Instant,
      swapsAcc: mutable.ArrayBuffer[SwapDetail] = mutable.ArrayBuffer.empty,
      seen: mutable.HashSet[String] = mutable.HashSet.empty
  ): EventStream[Either[CalibanClientError, Seq[SwapDetail]]] = {
    val lastTimestamp = swapsAcc.lastOption.map(_.timestamp)
    val lastId        = swapsAcc.lastOption.map(_.id)
    val swapRequest   = DEX.sushiPairSwapsQuery(pair, since, 0, lastTimestamp, lastId).toEventStream(SUSHI_SWAP_AVAX)
    swapRequest.flatMap {
      case error @ Left(_)  =>
        EventStream.fromValue(error)
      case Right(nextSwaps) =>
        if (nextSwaps.isEmpty || nextSwaps.forall(s => seen.contains(s.id))) {
          val finalSwaps = swapsAcc.distinct.sortWith(_.timestamp > _.timestamp)
          val asVector   = Vector.from(finalSwaps)
          EventStream.fromValue(Right(asVector))
        } else {
          nextSwaps.foreach(s => seen.add(s.id))
          pairSwapsPaginated(pair, since, swapsAcc ++ nextSwaps, seen)
        }
    }
  }
}
