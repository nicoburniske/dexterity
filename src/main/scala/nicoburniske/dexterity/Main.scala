package nicoburniske.dexterity

import java.time.Instant

import caliban.client.CalibanClientError
import caliban.client.laminext._
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import io.laminext.syntax.core._
import io.laminext.websocket.WebSocket
import nicoburniske.dexterity.components.material.{CircularProgress, Slider}
import nicoburniske.dexterity.exchange.DEX.PairAddress
import nicoburniske.dexterity.exchange.{DEX, SwapDetails}
import org.scalajs.dom

import scala.concurrent.duration._

object Main {
  val CONTAINER                  = "appContainer"
  val TABLE_MAX                  = 1000
  val MIN_HISTORY_INTERVAL_HOURS = 1.0
  val MAX_HISTORY_INTERVAL_HOURS = 8.0
  val SUSHISWAP_SUBGRAPH         = "wss://api.thegraph.com/subgraphs/name/sushiswap/avalanche-exchange"

  val websocket = WebSocket.url(SUSHISWAP_SUBGRAPH, "graphql-ws").graphql.build()

  val interval    = Var(1.hours)
  val minSwap     = Var(BigDecimal(10000))
  val swaps       = Var(Seq.empty[SwapDetails])
  val pairAddress = Var(PairAddress.SUSHI_WMEMO_MIM)

  val minSwapWriter     = minSwap.writer
  val pairAddressWriter = pairAddress.writer

  val $maybeSwaps: EventStream[Either[CalibanClientError, Seq[SwapDetails]]] =
    websocket.isConnected.combineWith(swaps.signal, pairAddress.signal).flatMap {
      case (false, _, _)       => emptyStream
      case (true, swaps, pair) =>
        val since = swaps
          .headOption
          .map(_.timestamp.toLong.seconds.toMillis)
          .map(Instant.ofEpochMilli)
          // Bulk-load history initially.
          .getOrElse(Instant.now().minusSeconds(MAX_HISTORY_INTERVAL_HOURS.hours.toSeconds))
        DEX.Subscriptions.pairSwapsSinceInstant(pair, since)(SwapDetails.DETAILS_MAPPED).toSubscription(websocket).received
    }

  /**
   * Add new swaps since last tick.
   */
  val swapUpdater: Observer[Seq[SwapDetails]] = swaps.updater((curr, next) => {
    // TODO: ensure curr and next are of same pair
    // TODO: remove transactions older than max duration?
    val oldest        = (Instant.now().toEpochMilli.millis - MAX_HISTORY_INTERVAL_HOURS.hours).toSeconds
    val removedTooOld = curr.reverse.dropWhile { swap => swap.timestamp < oldest }.reverse
    val nextSorted    = next.sortWith(_.timestamp > _.timestamp)
    (nextSorted ++ removedTooOld).distinctBy(_.id)
  })

  def emptyStream[A]: EventStream[A] = {
    EventStream.fromCustomSource[A](
      shouldStart = _ => false,
      start = (_, _, _, _) => (),
      stop = _ => ()
    )
  }

  val $swapsWithinInterval = swaps.signal.combineWith(interval.signal).map {
    case (allSwaps, interval) =>
      val since     = Instant.now().toEpochMilli.millis - interval
      val asSeconds = since.toSeconds
      allSwaps.takeWhile(_.timestamp > asSeconds)
  }

  val $swapsGreaterThanMin = $swapsWithinInterval.combineWith(minSwap.signal).map {
    case (swaps, min) =>
      swaps.filter { swap => swap.amountUSD.compare(min) >= 0 }
  }

  val contentOrLoading = $swapsWithinInterval.map { swaps =>
    if (swaps.isEmpty) {
      div(
        "Retrieving Swaps",
        CircularProgress().amend(
          CircularProgress.`indeterminate` := true
        ),
        cls := "center"
      )
    } else {
      content
    }
  }

  val sliderContent = div(
    L.span(child.text <-- interval.signal.map(interval => s"Volume Window = ${interval.toHours} hour(s)"), cls := "label"),
    L.span(
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
        Slider.styles.sliderBgColorBehindComponent := "grey",
        width                        := "50%"
      )),
    cls := "sliderContent"
  )

  val minTransaction = div(
    "Min swap size: ",
    input(
      typ := "text",
      placeholder <-- minSwap.signal.map(min => s"Min swap amount USD: $min"),
      controlled(
        value <-- minSwap.signal.map(_.toString),
        onInput.mapToValue.map(_.filter(Character.isDigit)).map(BigDecimal(_)) --> minSwapWriter
      ),
      cls := "inputBox"
    ),
    cls := "label"
  )

  val pairAddressInput = input(
    typ := "text",
    value <-- pairAddress,
    cls := "inputBox"
  )

  val inputPair = div(
    "Pair Address: ",
    pairAddressInput,
    button("Change Pair", onClick.mapTo(pairAddressInput.ref.value.toLowerCase) --> pairAddressWriter, cls := "button"),
    cls := "label"
  )

  val content = div(
    SwapStats($swapsWithinInterval),
    minTransaction,
    inputPair,
    sliderContent,
    children <-- SwapTable($swapsGreaterThanMin.map(_.take(TABLE_MAX)).changes),
    cls := "content"
  )

  val app = div(
    // EFFECTS.
    websocket.connect,
    websocket.connected --> (_ => websocket.init()),

    $maybeSwaps.collectLeft --> (error => println(s"${error.getMessage()}")),
    $maybeSwaps.collectRight --> swapUpdater,

    pairAddress.signal.changes --> (_ => swaps.set(Seq.empty)),
    // CONTENT.
    child <-- contentOrLoading
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), app) }(unsafeWindowOwner)
  }
}
