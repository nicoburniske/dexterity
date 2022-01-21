package nicoburniske.dexterity

import java.time.Instant

import com.raquo.laminar.api.L._
import nicoburniske.dexterity.exchange.SwapDetails
import nicoburniske.dexterity.task.OrderbookVolume
import org.scalajs.dom

import scala.collection.mutable
import scala.concurrent.duration._

object Main {
  val CONTAINER        = "appContainer"
  val TICK_INTERVAL    = 10.seconds
  val HISTORY_INTERVAL = 1.minutes

  val swaps = Var(Seq.empty[SwapDetails])

  // TODO: volume. Drag slider for timeframe. remove transactions older than timeframe.
  val writer: Observer[Seq[SwapDetails]] = swaps.updater((curr, next) => {
    next ++ curr
  })

  val $tick = EventStream.periodic(TICK_INTERVAL.toMillis.toInt)

  val newSwaps = $tick.flatMap { _ =>
    val currSwaps = swaps.now()
    val since     = currSwaps
      .headOption
      .map(_.timestamp.toLong.seconds.toMillis)
      .map(Instant.ofEpochMilli)
      .getOrElse(Instant.now().minusMillis(HISTORY_INTERVAL.toMillis))
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
    div(
      newSwaps --> writer,
      child <-- swapsToDiv
    )
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), myApp) }(unsafeWindowOwner)
  }
}
