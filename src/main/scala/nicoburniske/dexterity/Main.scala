package nicoburniske.dexterity

import caliban.client.laminext._
import com.raquo.laminar.api.L._
import io.laminext.websocket.WebSocket
import nicoburniske.dexterity.exchange.Subgraph
import nicoburniske.dexterity.exchange.Subgraph.Endpoint
import org.scalajs.dom

object Main {
  val CONTAINER = "appContainer"

  val subgraph: Var[Endpoint] = Var(Subgraph.Avax)

  //TODO: add chain switching.

  val $page = subgraph.signal.map(DexPage(_)).map(_.html)

  val app = div(
    child <-- $page
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), app) }(unsafeWindowOwner)
  }
}
