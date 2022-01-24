package nicoburniske.dexterity

import java.time.Instant

import com.raquo.laminar.api.L._
import nicoburniske.dexterity.components.material.{LinearProgressBar, Slider}
import nicoburniske.dexterity.exchange.SwapDetails
import nicoburniske.dexterity.task.OrderbookVolume
import org.scalajs.dom

import scala.collection.mutable
import scala.concurrent.duration._
import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode

object Main {
  val CONTAINER                  = "appContainer"
  val TICK_INTERVAL              = 10.seconds
  val MIN_HISTORY_INTERVAL_HOURS = 1.0
  val MAX_HISTORY_INTERVAL_HOURS = 8.0

  val $tick                          = EventStream.periodic(TICK_INTERVAL.toMillis.toInt)
  val interval: Var[FiniteDuration]  = Var(1.hours)
  val intervalSignal: Signal[Double] = interval.signal.map(_.toMinutes.toDouble)
  // val $updateSwaps = EventStream.merge($tick, intervalSignal.changes)

  val swaps = Var(Seq.empty[SwapDetails])

  /**
   * Add new swaps since last tick. Removes swaps that are older than {Now - Interval}.
   */
  val swapUpdater: Observer[Seq[SwapDetails]] = swaps.updater((curr, next) => {
    val since        = Instant.now().toEpochMilli.millis - interval.now()
    val removeTooOld = curr
      .reverse
      .dropWhile { swap =>
        val stamp = swap.timestamp.longValue.seconds
        stamp < since
      }
      .reverse
    next ++ removeTooOld
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

  def round(b: BigDecimal): BigDecimal = b.setScale(3, RoundingMode.HALF_UP)

  val sellsAndBuys = swaps.signal.map { swaps =>
    val grouped = swaps.groupMapReduce(_.isBuy)(_.amountUSD)(_ + _)
    (grouped.getOrElse(false, BigDecimal(0)), grouped.getOrElse(true, BigDecimal(0)))
  }

  val sellVolume  = sellsAndBuys.map(_._1).map(SwapDetails.roundAndFormat)
  val buyVolume   = sellsAndBuys.map(_._2).map(SwapDetails.roundAndFormat)
  val totalVolume = sellsAndBuys.map { case (s, b) => s + b }.map(SwapDetails.roundAndFormat)

  val sells     = swaps.signal.map(swaps => swaps.count(!_.isBuy))
  val buys      = swaps.signal.map(swaps => swaps.count(_.isBuy))
  val swapCount = swaps.signal.map(_.size)

  val volumeRatio = sellsAndBuys.signal.map {
    case (sells, buys) =>
      // No divide by 0.
      if (buys.equals(BigDecimal(0))) {
        BigDecimal(0.5)
      } else {
        val ratio = (buys - sells) / (sells + buys)
        round((ratio / 2) + 0.5)
      }
  }

  val newSwaps = $tick.flatMap { _ =>
    val currSwaps = swaps.now()
    val since     = currSwaps
      .headOption
      .map(_.timestamp.toLong.seconds.toMillis)
      .map(Instant.ofEpochMilli)
      .getOrElse(Instant.now().minusSeconds(interval.now().toSeconds))
    val seen      = mutable.HashSet.from(swaps.now().map(_.id))
    OrderbookVolume.wMemoSwapsPaginated(since, seen = seen).map {
      case Left(error)  =>
        println(s"Failed to retrieve swaps ${error.getMessage()}")
        Seq.empty
      case Right(value) =>
        println(s"${value.size} swaps found")
        value
    }
  }.filter(_.nonEmpty) // remove useless events to avoid recalculation.

  val swapsToTable = swaps.signal.map(createTable)

  val priceSignal = swaps.signal.map(_.headOption).map {
    case Some(value) => SwapDetails.roundAndFormat(value.realPrice)
    case None        => ""
  }

  val poolNameSignal = swaps.signal.map(_.headOption).map {
    case Some(value) => value.pair
    case None        => "Pair"
  }

  val token0Signal = swaps.signal.map(_.headOption).map {
    case Some(value) => value.token0
    case None        => "Token0"
  }

  val token1Signal = swaps.signal.map(_.headOption).map {
    case Some(value) => value.token1
    case None        => "Token1"
  }

  def createTable(swaps: Seq[SwapDetails]) = {
    val elements = swaps.map { swap =>
      val isBuy    = if (swap.isBuy) "BUY" else "SELL"
      val cssClass = if (swap.isBuy) "isBuy" else "isSell"
      ol(
        li(strong(isBuy)),
        li(SwapDetails.roundAndFormat(swap.amountUSD)),
        // li(SwapDetails.roundAndFormat(swap.realPrice)),
        li(swap.token0traded.toString()),
        li(swap.token1traded.toString()),
        li(swap.timeFormatted),
        li(a(href := swap.snowtraceLink, swap.realId.take(10), color.blue)),
        cls := ("tableRow", cssClass)
      )
    }
    val headers  = ol(
      li("Type"),
      li("Total USD"),
      li(child.text <-- token0Signal.map(_ + " swapped")),
      li(child.text <-- token1Signal.map(_ + " swapped")),
      li("Time"),
      li("Transaction"),
      cls := ("tableRow", "tableHeader")
    )
    headers +: elements
  }

  val myApp = div(
    cls := "app",
    // EFFECTS
    newSwaps --> swapUpdater,
    intervalSignal.changes --> (_ => resetSwaps()),
    // CONTENT
    h2(child.text <-- poolNameSignal, cls := "pairHeader"),
    div("Tick #: ", child.text <-- $tick.map(_.toString)),
    div("Total Swaps: ", child.text <-- swapCount),
    div("Total Volume: ", child.text <-- totalVolume),
    div("Total sell volume: ", child.text <-- sellVolume),
    div("Current price: ", child.text <-- priceSignal),
    div("Num sells: ", child.text <-- sells),
    div("Total buy volume: ", child.text <-- buyVolume),
    div("Num buys: ", child.text <-- buys),
    div(child <-- volumeRatio.map(_.toString)),
    Slider(
      _.pin                               := true,
      _.min                               := MIN_HISTORY_INTERVAL_HOURS,
      _.max                               := MAX_HISTORY_INTERVAL_HOURS,
      _.step                              := 0.5,
      _.value                             := 1,
      _.pin                               := true,
      slider => inContext { thisNode => slider.onChange.mapTo(thisNode.ref.value.hours) --> interval }
    ),
    LinearProgressBar(
      _.progress <-- volumeRatio.map(_.toDouble)
    ),
    div(
      children <-- swapsToTable
    )
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), myApp) }(unsafeWindowOwner)
  }
}
