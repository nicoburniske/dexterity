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
  val CONTAINER                    = "appContainer"
  val TICK_INTERVAL                = 10.seconds
  val MIN_HISTORY_INTERVAL_HOURS   = 1.0
  val MAX_HISTORY_INTERVAL_HOURS   = 8.0
  val SUSHISWAP_SUBGRAPH_WEBSOCKET = "ws://api.thegraph.com/subgraphs/name/sushiswap/avalanche-exchange"

  val interval    = Var(1.hours)
  val swaps       = Var(Seq.empty[SwapDetails])
  val pairAddress = Var(PairAddress.SUSHI_WMEMO_MIM)

  val websocket = WebSocket.url(SUSHISWAP_SUBGRAPH_WEBSOCKET, "graphql-ws").graphql.build()

  // TODO: Incorporate .changes here to prevent too many requests in websocket.
  val $maybeSwaps: EventStream[Either[CalibanClientError, Seq[SwapDetails]]] =
    websocket.isConnected.combineWith(swaps.signal).flatMap {
      case (false, _)    => emptyStream
      case (true, swaps) =>
        val since = swaps
          .headOption
          .map(_.timestamp.toLong.seconds.toMillis)
          .map(Instant.ofEpochMilli)
          // Bulk-load history initially.
          .getOrElse(Instant.now().minusSeconds(MAX_HISTORY_INTERVAL_HOURS.hours.toSeconds))
        DEX
          .Subscriptions
          .pairSwapsSinceInstant(pairAddress.now(), since)(SwapDetails.DETAILS_MAPPED)
          .toSubscription(websocket)
          .received
    }

  def emptyStream[A]: EventStream[A] = {
    EventStream.fromCustomSource[A](
      shouldStart = _ => false,
      start = (_, _, _, _) => (),
      stop = _ => ()
    )
  }

  val $newSwaps   = $maybeSwaps.collectRight
  val $swapErrors = $maybeSwaps.collectLeft

  val $swapsWithinInterval = swaps.signal.combineWith(interval.signal).map {
    case (allSwaps, interval) =>
      val since     = Instant.now().toEpochMilli.millis - interval
      val asSeconds = since.toSeconds
      allSwaps.takeWhile(_.timestamp > asSeconds)
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

  val contentOrLoading = $swapsWithinInterval.map { swaps =>
    if (swaps.isEmpty) {
      div(
        "Fetching History",
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
        width                        := "50%"
      )),
    cls := "sliderContent"
  )
  val content       = div(
    SwapStats($swapsWithinInterval),
    sliderContent,
    SwapTable($swapsWithinInterval),
    cls := "content"
  )
  val app           = div(
    // EFFECTS.
    websocket.connect,
    websocket.connected --> (_ => websocket.init()),
    $swapErrors --> (error => println(s"${error.getMessage()}")),
    $newSwaps --> swapUpdater,
    // CONTENT.
    child <-- contentOrLoading
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), app) }(unsafeWindowOwner)
  }
}
