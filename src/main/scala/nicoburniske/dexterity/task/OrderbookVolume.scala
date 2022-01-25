package nicoburniske.dexterity.task
import java.time.Instant

import caliban.client.CalibanClientError
import caliban.client.laminext._
import com.raquo.airstream.core.EventStream
import nicoburniske.dexterity.exchange.{DEX, SwapDetails}

import scala.collection.mutable

object OrderbookVolume {
  val SUSHI_SWAP_AVAX = "https://api.thegraph.com/subgraphs/name/sushiswap/avalanche-exchange"

  def pairSwapsPaginated(
      pair: String,
      since: Instant,
      swapsAcc: mutable.ArrayBuffer[SwapDetails] = mutable.ArrayBuffer.empty,
      seen: mutable.HashSet[String] = mutable.HashSet.empty
  ): EventStream[Either[CalibanClientError, Seq[SwapDetails]]] = {
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

//  def infoTask(msg: String): IO[Unit] = IO.delay(println(msg))
//
//  def findVolumeWMEMO(
//      duration: FiniteDuration
//  ): IO[(BigDecimal, BigDecimal)] = {
//    for {
//      allSwaps <- findSwapsWMEMO(duration)
//      _        <- infoTask(s"Found $allSwaps total swaps")
//    } yield calculateVolume(allSwaps)
//  }
//
//  def findSwapsWMEMO(duration: FiniteDuration): IO[Seq[SwapDetails]] = {
//    for {
//      since    <- IO.delay(Instant.now().minusMillis(duration.toMillis))
//      allSwaps <- wMemoSwapsPagination(since)
//    } yield allSwaps
//  }
//
//  def wMemoSwapsPagination(
//      since: Instant,
//      swapsAcc: mutable.ArrayBuffer[SwapDetails] = mutable.ArrayBuffer.empty,
//      seen: mutable.HashSet[String] = mutable.HashSet.empty
//  ): IO[Vector[SwapDetails]] = {
//    val lastTimestamp = swapsAcc.lastOption.map(_.timestamp)
//    val lastId        = swapsAcc.lastOption.map(_.id)
//    for {
//      response  <- DEX.wMemoSwapsRequest(since, 0, lastTimestamp, lastId).send(backend)
//      nextSwaps <- response.body match {
//                     case Left(value)  => IO.raiseError(value)
//                     case Right(swaps) => IO.pure(swaps)
//                   }
//      _         <- infoTask(s"Found ${nextSwaps.size} new swaps")
//      allSwaps  <- {
//        // Need to make sure that we don't get stuck in request loop if there are multiple transactions same time.
//        if (nextSwaps.isEmpty || nextSwaps.forall(s => seen.contains(s.id))) {
//          val finalSwaps = swapsAcc.distinct.sortWith(_.timestamp > _.timestamp)
//          val asVector   = Vector.from(finalSwaps)
//          IO.pure(asVector)
//        } else {
//          nextSwaps.foreach(s => seen.add(s.id))
//          wMemoSwapsPagination(since, swapsAcc ++ nextSwaps, seen)
//        }
//      }
//    } yield allSwaps
//  }
//
//  /**
//   * Calculates Sell and Buy volume for the given swaps.
//   *
//   * @param swaps
//   *   the swaps
//   * @return
//   *   (Sells, Buys)
//   */
//  def calculateVolume(swaps: Seq[SwapDetails]): (BigDecimal, BigDecimal) = {
//    val grouped = swaps.groupMapReduce(_.token0Received > 0)(_.amountUSD)(_ + _)
//    (grouped(false), grouped(true))
//  }
//
//  private def findVolumeConsole = {
//    findVolumeWMEMO(1.hours).map {
//      case (sells, buys) =>
//        (SwapDetails.roundAndFormat(sells), SwapDetails.roundAndFormat(buys))
//    }
//    // .foreach { case (sells, buys) => println(s"Sells $sells, Buys $buys") }
//  }
}
