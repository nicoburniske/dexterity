package nicoburniske.dexterity.exchange

import generated.sushi.exchange.Pair

object PairDetail {
  val SELECTION_BUILDER = Pair.id ~ Pair.name
  val DETAILS_MAPPED = SELECTION_BUILDER.mapN(PairDetail.apply _)
}

case class PairDetail (id: String, name: String) {

}
