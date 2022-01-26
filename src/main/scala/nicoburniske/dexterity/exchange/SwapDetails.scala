package nicoburniske.dexterity.exchange

import generated.sushi.exchange.{Pair, Swap, Token}

import scala.concurrent.duration._
import scala.math.BigDecimal.RoundingMode
import scala.scalajs.js

object SwapDetails {
  val SELECTION_BUILDER =
    Swap.pair(Pair.name) ~
      Swap.pair(Pair.token0Price) ~
      Swap.pair(Pair.token1Price) ~
      Swap.pair(Pair.token0(Token.name)) ~
      Swap.pair(Pair.token1(Token.name)) ~
      Swap.id ~
      Swap.timestamp ~
      Swap.amountUSD ~
      Swap.amount0In ~
      Swap.amount0Out ~
      Swap.amount1In ~
      Swap.amount1Out ~
      Swap.to

  val DETAILS_MAPPED = SELECTION_BUILDER.mapN(SwapDetails.apply _)
  val FORMATTER      = java.text.NumberFormat.getCurrencyInstance

  def round(d: BigDecimal, scale: Int = 2): BigDecimal = {
    d.setScale(scale, RoundingMode.HALF_UP)
  }

  /**
   * Number sign gets printed fairly weirdly. Remove and re-add it explicitly.
   * @param d
   *   the number to round and format
   * @return
   *   a dollar representation of the value
   */
  def roundAndFormat(d: BigDecimal): String = {
    val s = FORMATTER.format(round(d).toDouble)
    "$" + s.substring(1, s.length)
  }
}

case class SwapDetails(
    pair: String,
    price0: BigDecimal,
    price1: BigDecimal,
    fullName0: String,
    fullName1: String,
    id: String,
    timestamp: BigInt,
    amountUSD: BigDecimal,
    token0Sold: BigDecimal,
    token0Received: BigDecimal,
    token1Sold: BigDecimal,
    token1Received: BigDecimal,
    sender: String) {

  val (token0, token1) = {
    val split = pair.split("-")
    (split(0), split(1))
  }

  val isBuy = token0Received > 0

  // TODO: review. not true always
  val realPrice     = if (price0 > price1) price0 else price1
  // TODO: why is there a dash? Deserialization perhaps?
  val realId        = id.split("-").head
  val snowtraceLink = s"https://snowtrace.io/tx/$realId"
  val debankLink = s"https://debank.com/profile/$sender"

  val timeFormatted     = {
    val instant = new js.Date(timestamp.toLong.seconds.toMillis)
    instant.toLocaleTimeString()
  }

  val token0traded = {
    val traded = if (token0Received == 0) token0Sold else token0Received
    traded.setScale(3, RoundingMode.HALF_UP)
  }

  val token1traded = {
    val traded = if (token1Received == 0) token1Sold else token1Received
    traded.setScale(3, RoundingMode.HALF_UP)
  }
}
