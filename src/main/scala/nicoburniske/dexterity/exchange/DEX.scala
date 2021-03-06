package nicoburniske.dexterity.exchange

import java.time.Instant

import caliban.client.FieldBuilder.{ListOf, Obj, OptionOf}
import caliban.client.Operations.{RootQuery, RootSubscription}
import caliban.client.SelectionBuilder.Field
import caliban.client.__Value.__ObjectValue
import caliban.client.{ArgEncoder, Argument, SelectionBuilder}
import generated.sushi.exchange.{Pair, Swap}

import scala.concurrent.duration._
import scala.math.BigDecimal.RoundingMode

/**
 * Supplies query mechanisms for DEXs with `The Graph` API compatibility.
 */
object DEX {
  implicit def encodeMap[V](implicit encodeVal: ArgEncoder[V]): ArgEncoder[Seq[(String, V)]] = (seq: Seq[(String, V)]) =>
    __ObjectValue(seq.toList.map { case (key, value) => key -> encodeVal.encode(value) })

  object PairAddress {
    val SUSHI_WMEMO_MIM = "0x4d308c46ea9f234ea515cc51f16fba776451cac8"
    val SUSHI_WAVAX_MIM = "0xcbb424fd93cdec0ef330d8a8c985e8b147f62339"
  }

  /**
   * Graphql Queries for DEX `The Graph` API.
   */
  object Queries {
    def pairData[A](pairId: String)(innerSelection: SelectionBuilder[Pair, A]): SelectionBuilder[RootQuery, Option[A]] =
      Field("pair", OptionOf(Obj(innerSelection)), arguments = List(Argument("id", pairId, "String!")))

    def pairs[A](innerSelection: SelectionBuilder[Pair,A]): SelectionBuilder[RootQuery, Seq[A]] = {
      val orderBy = Argument("orderBy", "volumeUSD", "")
      val sortDirection = Argument("orderDirection", "desc", "")
      Field("pairs", ListOf(Obj(innerSelection)), arguments = List(orderBy, sortDirection))
    }

    def pairSwapsSinceInstant[A](
        pairId: String,
        instant: Instant,
        minTradeAmount: BigInt = BigInt(0),
        lastTimestamp: Option[BigInt] = None,
        lastId: Option[String] = None,
        sortAscending: Boolean = false)(swapSelection: SelectionBuilder[Swap, A]): SelectionBuilder[RootQuery, Seq[A]] = {
      val instantToSec   = BigInt(instant.toEpochMilli.millis.toSeconds)
      val maybeTimestamp = lastTimestamp.map(_.toString).map("timestamp_lte" -> _)
      val maybeId        = lastId.map("id_not" -> _)
      val conditions     = Seq(
        Some("timestamp_gte" -> instantToSec.toString),
        Some("amountUSD_gte" -> minTradeAmount.toString),
        maybeTimestamp,
        maybeId
      ).flatten
      val filterArg      = Argument("where", conditions, "")
      val orderByArg     = Argument("orderBy", "timestamp", "")
      val direction      = if (sortAscending) "asc" else "desc"
      val sortDirection  = Argument("orderDirection", direction, "")
      val swapsField     = Field[Pair, List[A]](
        "swaps",
        ListOf(Obj(swapSelection)),
        arguments = List(filterArg, orderByArg, sortDirection)
      )
      Field("pair", Obj(swapsField), arguments = List(Argument("id", pairId, "String!")))
    }
  }

  // TODO: Ensure casting is safe.
  object Subscriptions {

    def pairSwapsSinceInstant[A](
        pairId: String,
        instant: Instant,
        minTradeAmount: BigInt = BigInt(0),
        lastTimestamp: Option[BigInt] = None,
        lastId: Option[String] = None)(swapSelection: SelectionBuilder[Swap, A]): SelectionBuilder[RootSubscription, Seq[A]] = {
      Queries
        .pairSwapsSinceInstant(pairId, instant, minTradeAmount, lastTimestamp, lastId, sortAscending = true)(swapSelection)
        .asInstanceOf[SelectionBuilder[RootSubscription, Seq[A]]]
    }
  }

  def queryPriceWMEMO(): SelectionBuilder[RootQuery, Option[(String, BigDecimal)]] = {
    Queries.pairData(PairAddress.SUSHI_WMEMO_MIM)(Pair.name ~ Pair.token1Price)
  }

  def sushiPairSwapsQuery(
      pairAddress: String,
      since: Instant,
      minSwap: BigInt,
      lastTimestamp: Option[BigInt] = None,
      lastId: Option[String] = None): SelectionBuilder[RootQuery, Seq[SwapDetail]] = {
    Queries.pairSwapsSinceInstant(pairAddress, since, minSwap, lastTimestamp, lastId)(SwapDetail.DETAILS_MAPPED)
  }

  private def round(res: Option[(String, BigDecimal)]): Option[(String, BigDecimal)] = {
    res.map { case (label, price) => (label, price.setScale(3, RoundingMode.HALF_UP)) }
  }
}
