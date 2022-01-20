package nicoburniske.dexterity.exchange


import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId, ZonedDateTime}

import generated.sushi.exchange.{Pair, Swap, Token}

import scala.concurrent.duration._
import scala.math.BigDecimal.RoundingMode

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
      Swap.amount1Out

  val DETAILS_MAPPED = SELECTION_BUILDER.mapN(SwapDetails.apply _)
  val FORMATTER      = java.text.NumberFormat.getCurrencyInstance

  def round(d: BigDecimal, scale: Int = 2): BigDecimal = {
    d.setScale(scale, RoundingMode.HALF_UP)
  }

  def roundAndFormat(d: BigDecimal): String = {
    FORMATTER.format(round(d).toDouble)
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
                        token1Received: BigDecimal) {

  import SwapDetails.{roundAndFormat, round}

  val (token0, token1) = {
    val split = pair.split("-")
    (split(0), split(1))
  }

  // TODO: review. not true always
  val realPrice     = if (price0 > price1) price0 else price1
  // TODO: why is there a dash? Deserialization perhaps?
  val realId = id.split("-").head
  val snowtraceLink = s"https://snowtrace.io/tx/$realId"

  val timeFormatted = {
    val instant  = Instant.ofEpochMilli(timestamp.toLong.seconds.toMillis)
    val dateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"))
    DateTimeFormatter.ISO_ZONED_DATE_TIME.format(dateTime)
  }
  val swapDetails   = {
    if (token0Sold.compare(0) == 0) {
      s"${round(token1Sold)} $token1 swapped for ${round(token0Received)} $token0"
    } else {
      s"${round(token0Sold)} $token0 swapped for ${round(token1Received)} $token1"
    }
  }

  val message =
    s"""
       | $pair Whale Alert 🚨
       |- Transaction Amount: ${roundAndFormat(amountUSD)}
       |- Timestamp: $timeFormatted
       |- Price: ${roundAndFormat(realPrice)}
       |- $swapDetails
       |- $snowtraceLink
       |""".stripMargin.strip
}
