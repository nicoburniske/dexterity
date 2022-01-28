package nicoburniske.dexterity

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._
import nicoburniske.dexterity.exchange.Subgraph
import nicoburniske.dexterity.exchange.Subgraph.Endpoint
import org.scalajs.dom

object Main {
  val CONTAINER = "appContainer"

  val selectedSubgraph: Var[Endpoint] = Var(Subgraph.Avax)

  val $page = selectedSubgraph.signal.map(DexPage(_)).map(_.html)

  val selectNetwork = selectedSubgraph.signal.map { selected =>
    Subgraph.ALL.map { subgraph =>
      val isSelected = subgraph == selected
      div(
        L.span(subgraph.name, cls := "label"),
        input(typ                 := "checkbox", onChange.mapTo(subgraph) --> selectedSubgraph, checked := isSelected)
      )
    }
  }

  val app = div(
    div("Networks: ", cls:= "label", color.green),
    child <-- selectNetwork.map(div(_)),
    child <-- $page
  )

  def main(args: Array[String]): Unit = {
    documentEvents.onDomContentLoaded.foreach { _ => render(dom.document.getElementById(CONTAINER), app) }(unsafeWindowOwner)
  }
}
