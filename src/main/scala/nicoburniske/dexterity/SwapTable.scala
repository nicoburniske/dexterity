package nicoburniske.dexterity

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L._
import nicoburniske.dexterity.exchange.SwapDetails

object SwapTable {
  def apply(swapSignal: Signal[Seq[SwapDetails]]): HtmlElement = {
    div(
      children <-- swapSignal.map(toRows)
    )
  }
  def toRows(swaps: Seq[SwapDetails]): Seq[HtmlElement] = {
    val token0 = swaps.headOption.map(_.token0).map(_ + " swapped").getOrElse("Token0")
    val token1 = swaps.headOption.map(_.token1).map(_ + " swapped").getOrElse("Token1")
    val elements = swaps.map { swap =>
      val isBuy    = if (swap.isBuy) "BUY" else "SELL"
      val cssClass = if (swap.isBuy) "isBuy" else "isSell"
      ol(
        li(strong(isBuy)),
        li(SwapDetails.roundAndFormat(swap.amountUSD)),
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
      li(token0),
      li(token1),
      li("Time"),
      li("Transaction"),
      cls := ("tableRow", "tableHeader")
    )
    headers +: elements
  }
}
