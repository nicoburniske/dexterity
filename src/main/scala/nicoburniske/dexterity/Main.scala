package nicoburniske.dexterity

import cats.effect.IO
import com.raquo.laminar.api.L._
import nicoburniske.dexterity.task.OrderbookVolume
import org.scalajs.dom

import scala.concurrent.duration._

object Main {
  val CONTAINER        = "appContainer"
  val TICK_INTERVAL    = 1.minute
  val HISTORY_INTERVAL = 5.minutes
  implicit val runtime          = cats.effect.unsafe.IORuntime.global
  import scala.concurrent.ExecutionContext.Implicits.global

  val myApp = {

    val $tick     = EventStream.periodic(TICK_INTERVAL.toMillis.toInt)
    // val something = $tick.foldLeft(Seq.empty[SwapDetails])(refreshSwaps)
    val something = $tick.flatMap(_ => ioToStream(OrderbookVolume.findSwapsWMEMO(HISTORY_INTERVAL))).map(swaps =>
      swaps.map(_.message).mkString("\n"))

    div(
      div("Tick #: ", child.text <-- $tick.map(_.toString)),
      div(
        "Random #: ",
        child.text <-- $tick.mapTo((scala.util.Random.nextInt() % 100).toString)
      ),
      div(
        child <-- something.map(div(_))
      )
    )
  }
//  def refreshSwaps(swaps: Seq[SwapDetails], tick : Int): Seq[SwapDetails] = {
//  }

  def ioToStream[A](io: IO[A]): EventStream[A] = {
    EventStream.fromValue((), emitOnce = false).flatMap(_ => io.unsafeToFuture()(runtime))(SwitchFutureStrategy)
  }

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), myApp) }(unsafeWindowOwner)
  }
}
