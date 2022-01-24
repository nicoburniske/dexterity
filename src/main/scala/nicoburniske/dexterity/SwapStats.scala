package nicoburniske.dexterity

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L._
import nicoburniske.dexterity.components.material.LinearProgressBar
import nicoburniske.dexterity.exchange.SwapDetails

import scala.math.BigDecimal
import scala.math.BigDecimal.RoundingMode

object SwapStats {
  def round(b: BigDecimal): BigDecimal = b.setScale(3, RoundingMode.HALF_UP)

  def apply(swaps: Signal[Seq[SwapDetails]]): HtmlElement = {

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
    val priceSignal = swaps.map(_.headOption).map {
      case Some(value) => SwapDetails.roundAndFormat(value.realPrice)
      case None        => ""
    }

    val poolNameSignal = swaps.map(_.headOption).map {
      case Some(value) => value.pair
      case None        => "Pair"
    }

    val tableStats = Seq(
      h4("Total Swaps: ", child.text <-- swapCount),
      h4("Total Volume: ", child.text <-- totalVolume),
      h4("Total sell volume: ", child.text <-- sellVolume),
      h4("Total buy volume: ", child.text <-- buyVolume),
      h4("Current price: ", child.text <-- priceSignal),
      h4("Num sells: ", child.text <-- sells),
      h4("Num buys: ", child.text <-- buys),
      h4(child <-- volumeRatio.map(_.toString))
    ).map(_.amend(cls := "stat"))

    div(
      h2(child.text <-- poolNameSignal, cls                := "pairHeader"),
      tableStats,
      LinearProgressBar(
        _.progress <-- volumeRatio.map(_.toDouble)
      ).amend(
        LinearProgressBar.styles.themePrimary              := "green",
        LinearProgressBar.styles.linearProgressBufferColor := "red"
      )
    )
  }
}
