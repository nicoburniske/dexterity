package nicoburniske.dexterity

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L._
import nicoburniske.dexterity.exchange.SwapDetails

object SwapTable {

  def apply(swaps: EventStream[Seq[SwapDetails]]): EventStream[Seq[HtmlElement]] = {
    for {
      rows <- swaps.split(_.id)(renderSwapAsRow)
      headers <- swaps.map(renderHeaders)
    } yield headers +: rows
  }

  def renderSwapAsRow(id: String, swap: SwapDetails, swapStream: EventStream[SwapDetails]): HtmlElement = {
    val isBuy    = if (swap.isBuy) "BUY" else "SELL"
    val cssClass = if (swap.isBuy) "isBuy" else "isSell"
    ol(
      li(strong(isBuy)),
      li(SwapDetails.roundAndFormat(swap.amountUSD)),
      li(swap.token0traded.toString()),
      li(swap.token1traded.toString()),
      li(swap.timeFormatted),
      li(a(href := swap.snowtraceLink, swap.realId.take(10), color.blue)),
      li(a(href := swap.debankLink, swap.sender.take(10), color.blue)),
      cls := ("tableRow", cssClass)
    )
  }

  def renderHeaders(swaps: Seq[SwapDetails]): HtmlElement = {
    val token0   = swaps.headOption.map(_.token0).map(_ + " swapped").getOrElse("Token0")
    val token1   = swaps.headOption.map(_.token1).map(_ + " swapped").getOrElse("Token1")

    ol(
      li("Type"),
      li("Total USD"),
      li(token0),
      li(token1),
      li("Time"),
      li("Transaction"),
      li("From"),
      cls := ("tableRow", "tableHeader")
    )
  }
}
