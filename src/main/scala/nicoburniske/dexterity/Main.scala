package nicoburniske.dexterity

import java.time.Instant

import com.raquo.laminar.api.L._
import nicoburniske.dexterity.components.material.Slider
import nicoburniske.dexterity.exchange.DEX.PairAddress
import nicoburniske.dexterity.exchange.SwapDetails
import nicoburniske.dexterity.task.OrderbookVolume
import org.scalajs.dom

import scala.collection.mutable
import scala.concurrent.duration._

object Main {
  val CONTAINER                  = "appContainer"
  val TICK_INTERVAL              = 10.seconds
  val MIN_HISTORY_INTERVAL_HOURS = 1.0
  val MAX_HISTORY_INTERVAL_HOURS = 8.0

  val $tick                         = EventStream.periodic(TICK_INTERVAL.toMillis.toInt)
  val interval: Var[FiniteDuration] = Var(1.hours)
  // val $updateSwaps = EventStream.merge($tick, intervalSignal.changes)

  val swaps       = Var(Seq.empty[SwapDetails])
  val pairAddress = Var(PairAddress.SUSHI_WMEMO_MIM)

  /**
   * Add new swaps since last tick. Removes swaps that are older than {Now - Interval}.
   */
  val swapUpdater: Observer[Seq[SwapDetails]] = swaps.updater((curr, next) => {
    // TODO: ensure curr and next are of same pair.
    val since        = Instant.now().toEpochMilli.millis - interval.now()
    val removeTooOld = curr
      .reverse
      .dropWhile { swap =>
        val stamp = swap.timestamp.longValue.seconds
        stamp < since
      }
      .reverse
    (next ++ removeTooOld).distinct
  })

  /**
   * TODO: see if can replace tick.resetTo with merged event stream.
   *
   * Reset all swaps and emit a tick event (force refresh of swaps).
   */
  def resetSwaps(): Unit = {
    swaps.set(Seq.empty)
    $tick.resetTo(0)
  }

  val newSwaps = $tick.map(_ => pairAddress.now() -> swaps.now())
    .flatMap { case (pair, swaps) => findNewSwaps(pair, swaps) }
    .filter(_.nonEmpty) // remove useless events to avoid recalculation.

  def findNewSwaps(pairAddress: String, oldSwaps: Seq[SwapDetails]): EventStream[Seq[SwapDetails]] = {
    val since = oldSwaps
      .headOption
      .map(_.timestamp.toLong.seconds.toMillis)
      .map(Instant.ofEpochMilli)
      .getOrElse(Instant.now().minusSeconds(interval.now().toSeconds))
    val seen  = mutable.HashSet.from(oldSwaps.map(_.id))
    OrderbookVolume.pairSwapsPaginated(pairAddress, since, seen = seen).map {
      case Left(error)  =>
        println(s"Failed to retrieve swaps ${error.getMessage()}")
        Seq.empty
      case Right(value) =>
        println(s"${value.size} swaps found")
        value
    }
  }

  val app = div(
    // EFFECTS.
    newSwaps --> swapUpdater,
    interval.signal.changes --> (_ => resetSwaps()),
    // CONTENT.
    h4("Tick #: ", child.text <-- $tick.map(_.toString)),
    SwapStats(swaps.signal),
    Slider(
      _.pin   := true,
      _.min   := MIN_HISTORY_INTERVAL_HOURS,
      _.max   := MAX_HISTORY_INTERVAL_HOURS,
      _.step  := 0.5,
      _.value := 1,
      _.pin   := true,
      slider => inContext { thisNode => slider.onChange.mapTo(thisNode.ref.value.hours) --> interval }
    ).amend(
      Slider.styles.themeSecondary := "rebeccapurple",
      width                        := "50%"
    ),
    SwapTable(swaps.signal),
    // STYLING.
    cls := "app"
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), app) }(unsafeWindowOwner)
  }
}
