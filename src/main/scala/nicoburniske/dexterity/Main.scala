package nicoburniske.dexterity

import java.time.Instant

import com.github.uosis.laminar.webcomponents.material.{LinearProgress, Slider}
import com.raquo.laminar.api.L._
import nicoburniske.dexterity.exchange.SwapDetails
import nicoburniske.dexterity.task.OrderbookVolume
import org.scalajs.dom

import scala.collection.mutable
import scala.concurrent.duration._
import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode

object Main {
  val CONTAINER            = "appContainer"
  val TICK_INTERVAL        = 10.seconds
  val MIN_HISTORY_INTERVAL = 1.hours
  val MAX_HISTORY_INTERVAL = 4.hours

  val swaps    = Var(Seq.empty[SwapDetails])
  val interval = Var(MIN_HISTORY_INTERVAL.toSeconds)

  val writer: Observer[Seq[SwapDetails]] = swaps.updater((curr, next) => {
    next ++ curr
  })

  def round(b: BigDecimal): BigDecimal = b.setScale(3, RoundingMode.HALF_UP)

  val sellsAndBuys = swaps.signal.map { swaps =>
    val grouped             = swaps.groupMapReduce(_.isBuy)(_.amountUSD)(_ + _)
    (grouped.getOrElse(false, BigDecimal(0)), grouped.getOrElse(true, BigDecimal(0)))
  }
  val sellVolume   = sellsAndBuys.map(_._1).map(round).map(_.toString)
  val buyVolume    = sellsAndBuys.map(_._2).map(round).map(_.toString)

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

  val $tick = EventStream.periodic(TICK_INTERVAL.toMillis.toInt)

  val newSwaps = $tick.flatMap { _ =>
    val currSwaps = swaps.now()
    val since     = currSwaps
      .headOption
      .map(_.timestamp.toLong.seconds.toMillis)
      .map(Instant.ofEpochMilli)
      .getOrElse(Instant.now().minusSeconds(interval.now()))
    val seen      = mutable.HashSet.from(swaps.now().map(_.id))
    OrderbookVolume.wMemoSwapsPaginated(since, seen = seen).map {
      case Left(error)  =>
        println(s"FUCK ${error.getMessage()}")
        Seq.empty
      case Right(value) =>
        println(s"${value.size} swaps found")
        value
    }
  }

  val swapsToDiv = swaps.signal.map { swaps => div(swaps.map(_.message).map(div(_))) }

  val myApp = div(
    div("Tick #: ", child.text <-- $tick.map(_.toString)),
    div("Total Swaps: ", child.text <-- swapCount),
    div("Total sell volume: ", child.text <-- sellVolume),
    div("Num sells: ", child.text <-- sells),
    div("Total buy volume: ", child.text <-- buyVolume),
    div("Num buys: ", child.text <-- buys),
    div(child <-- volumeRatio.map(_.toString)),
    Slider(
      _.pin  := true,
      _.min  := 1,
      _.max  := 4,
      _.step := 1
      // _.onChange.map{v => v.}
    ),
    LinearProgress(
      _.progress <-- volumeRatio.map(_.toDouble)
    ),
    div(
      newSwaps --> writer,
      child <-- swapsToDiv
    )
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), myApp) }(unsafeWindowOwner)
  }
}
