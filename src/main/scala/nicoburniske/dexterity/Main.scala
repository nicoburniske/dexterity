package nicoburniske.dexterity

import java.time.Instant

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import nicoburniske.dexterity.components.material.{LinearProgressBar, ListItem, Slider}
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

  val swapsToList           = swaps.signal.map { swaps => div(swaps.map(_.message).map(div(_))) }
  val swapsToListComponents = swaps.signal.map(swapList)

  def swapList(swaps: Seq[SwapDetails]) = {
    val elements = swaps.map { swap =>
      ListItem(
        // TODO: wrap in div?
        _.slots.default(
            L.span(if (swap.isBuy) "BUY" else "SELL", if (swap.isBuy) color.green else color.red),
            L.span(SwapDetails.roundAndFormat(swap.amountUSD)),
            L.span(swap.timeFormatted),
            L.a(href := swap.snowtraceLink)
          )
        )
    }
    components
      .material
      .List(
        _.slots.default(elements: _*)
      )
  }

  val priceStream = swaps.signal.map { swaps =>
    swaps.headOption match {
      case Some(value) => SwapDetails.roundAndFormat(value.realPrice)
      case None        => ""
    }
  }

  val myApp = div(
    div("Tick #: ", child.text <-- $tick.map(_.toString)),
    div("Total Swaps: ", child.text <-- swapCount),
    div("Total Volume: ", child.text <-- totalVolume),
    div("Total sell volume: ", child.text <-- sellVolume),
    div("Current price: ", child.text <-- priceStream),
    div("Num sells: ", child.text <-- sells),
    div("Total buy volume: ", child.text <-- buyVolume),
    div("Num buys: ", child.text <-- buys),
    div(child <-- volumeRatio.map(_.toString)),
    Slider(
      _.pin   := true,
      _.min   := MIN_HISTORY_INTERVAL_HOURS,
      _.max   := MAX_HISTORY_INTERVAL_HOURS,
      _.step  := 0.5,
      _.value := 1,
      _.pin   := true,
      slider => inContext { thisNode => slider.onChange.mapTo(thisNode.ref.value.hours) --> interval }
    ),
    LinearProgressBar(
      _.progress <-- volumeRatio.map(_.toDouble)
    ),
    div(
      newSwaps --> swapUpdater,
      intervalSignal.changes --> (_ => resetSwaps()),
      child <-- swapsToListComponents
    )
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), myApp) }(unsafeWindowOwner)
  }
}
