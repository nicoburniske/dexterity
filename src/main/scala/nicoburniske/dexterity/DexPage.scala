package nicoburniske.dexterity

import java.time.Instant

import caliban.client.Operations.RootSubscription
import caliban.client.SelectionBuilder
import caliban.client.laminext._
import caliban.client.ws.{GraphQLWSRequest, GraphQLWSResponse}
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import io.laminext.syntax.core._
import io.laminext.websocket.WebSocket
import nicoburniske.dexterity.components.material.{CircularProgress, Slider}
import nicoburniske.dexterity.exchange.Subgraph.Endpoint
import nicoburniske.dexterity.exchange.{DEX, PairDetail, SwapDetail}

import scala.concurrent.duration._

object DexPage {
  type WebSocketGraphQL = WebSocket[GraphQLWSResponse, GraphQLWSRequest]

  val TABLE_MAX                  = 1000
  val MIN_HISTORY_INTERVAL_HOURS = 1.0
  val MAX_HISTORY_INTERVAL_HOURS = 5.0
}

case class DexPage(dexEndpoint: Endpoint) {
  import DexPage._
  val websocket = WebSocket.url(dexEndpoint.wss, "graphql-ws").graphql.build()

  val interval     = Var(1.hours)
  val minSwap      = Var(BigDecimal(10000))
  val allPairs     = Var(Seq.empty[PairDetail])
  val selectedPair = Var("")
  val swaps        = Var(Seq.empty[SwapDetail])

  /**
   * Add new swaps since last tick.
   */
  val swapUpdater: Observer[Seq[SwapDetail]] = swaps.updater { (curr, next) =>
    val oldest        = (Instant.now().toEpochMilli.millis - MAX_HISTORY_INTERVAL_HOURS.hours).toSeconds
    val removedTooOld = curr.reverse.dropWhile { swap => swap.timestamp < oldest }.reverse
    val nextSorted    = next.sortWith(_.timestamp > _.timestamp)
    (nextSorted ++ removedTooOld).distinctBy(_.id)
  }

  val $maybeTopPairs = {
    val query = DEX.Queries.pairs(PairDetail.DETAILS_MAPPED)
    query.toEventStream(dexEndpoint.https)
  }

  // TODO: Incorporate .changes (eventstream) instead of signal?
  val $maybeSwaps = websocket.isConnected.combineWith(selectedPair.signal, swaps.signal).flatMap {
    case (false, _, _)       => emptyStream
    case (true, "", _)       => emptyStream
    case (true, pair, swaps) =>
      val since = swaps
        .headOption
        .map(_.timestamp.toLong.seconds.toMillis)
        .map(Instant.ofEpochMilli)
        // Bulk-load history initially.
        .getOrElse(Instant.now().minusSeconds(MAX_HISTORY_INTERVAL_HOURS.hours.toSeconds))
      val query: SelectionBuilder[RootSubscription, Seq[SwapDetail]] =
        DEX.Subscriptions.pairSwapsSinceInstant(pair, since)(SwapDetail.DETAILS_MAPPED)
      // TODO: inquire about unsubscribing?
      query.toSubscription(websocket).received
  }

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
        CircularProgress().amend(CircularProgress.`indeterminate` := true),
        cls := "center"
      )
    } else {
      content
    }
  }

  val sliderContent = div(
    L.span(child.text <-- interval.signal.map(interval => s"Volume Window = ${interval.toHours} hour(s) "), cls := "label"),
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
        Slider.styles.themeSecondary               := "rebeccapurple",
        Slider.styles.sliderBgColorBehindComponent := "grey",
        width                                      := "50%"
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
        onInput.mapToValue.map(_.filter(Character.isDigit)).map {
          case ""    => BigDecimal(0) // Case to handle no input
          case other => BigDecimal(other)
        } --> minSwap
      ),
      cls := "inputBox"
    ),
    cls := "label"
  )

  val pairAddressInput = input(
    typ := "text",
    value <-- selectedPair,
    cls := "inputBox"
  )

  val inputPair = selectedPair.signal.combineWith(allPairs.signal).map {
    case (selected, all) =>
      val options = all.take(10).map { pair =>
        val isSelected = pair.id == selected
        div(
          L.span(pair.name, cls := "label"),
          input(typ             := "checkbox", onChange.mapTo(pair.id) --> selectedPair, checked := isSelected))
      }
      div(
        div("Top 10 Pairs by Volume: ", cls:= "label", color.green),
        options
      )
  }

  val content = div(
    child <-- inputPair,
    SwapStats($swapsWithinInterval),
    minTransaction,
    sliderContent,
    children <-- SwapTable($swapsGreaterThanMin.map(_.take(TABLE_MAX)).changes),
    cls := "content"
  )

  val html = div(
    // Initialize websocket connection.
    websocket.connect,
    websocket.connected --> (_ => websocket.init()),

    // Find top pairs and default to pair with highest liquidity.
    $maybeTopPairs.collectLeft --> (error => println(s"Couldn't retrieve top pairs ${error.getMessage()}")),
    $maybeTopPairs.collectRight --> allPairs,
    allPairs.signal.changes.filter(_.nonEmpty).map(_.head.id) --> selectedPair,
    selectedPair.signal.changes --> (_ => swaps.set(Seq.empty)),

    // Find swaps with WebSocket.
    $maybeSwaps.collectLeft --> (error => println(s" Failed to retrieve swaps ${error.getMessage()}")),
    $maybeSwaps.collectRight --> swapUpdater,

    // Load content.
    child <-- contentOrLoading
  )
}
