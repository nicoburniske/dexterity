package nicoburniske.dexterity.exchange

import java.time.Instant

import caliban.client.FieldBuilder.{ListOf, Obj, OptionOf}
import caliban.client.Operations.RootQuery
import caliban.client.SelectionBuilder.Field
import caliban.client.__Value.__ObjectValue
import caliban.client.{ArgEncoder, Argument, CalibanClientError, SelectionBuilder}
import generated.sushi.exchange.{Pair, Swap}
import sttp.client3.{Request, _}

import scala.concurrent.duration._
import scala.math.BigDecimal.RoundingMode

/**
 * Supplies query mechanisms for DEXs with `The Graph` API compatibility.
 */
object DEX {
  implicit def encodeMap[V](implicit encodeVal: ArgEncoder[V]): ArgEncoder[Seq[(String, V)]] = (seq: Seq[(String, V)]) =>
    __ObjectValue(seq.toList.map { case (key, value) => key -> encodeVal.encode(value) })

  object Endpoints {
    val TRADER_JOE = uri"https://api.thegraph.com/subgraphs/name/token-terminal/trader-joe-v1-avalanche"
    val SUSHISWAP  = uri"https://api.thegraph.com/subgraphs/name/sushiswap/avalanche-exchange"
  }

  object PairAddress {
    val TJ_TIME_MIM     = "0x113f413371fc4cc4c9d6416cf1de9dfd7bf747df"
    val SUSHI_WMEMO_MIM = "0x4d308c46ea9f234ea515cc51f16fba776451cac8"
  }

  /**
   * Graphql Queries for DEX `The Graph` API.
   */
  object Queries {
    def pairData[A](pairId: String)(innerSelection: SelectionBuilder[Pair, A]): SelectionBuilder[RootQuery, Option[A]] =
      Field("pair", OptionOf(Obj(innerSelection)), arguments = List(Argument("id", pairId, "String!")))

    def pairSwaps[A](pairId: String)(innerSelection: SelectionBuilder[Swap, A]): SelectionBuilder[RootQuery, Seq[A]] =
      Field("pair", Obj(Pair.swaps(innerSelection)), arguments = List(Argument("id", pairId, "String!")))

    def pairSwapsSinceInstant[A](
        pairId: String,
        instant: Instant,
        minTradeAmount: BigInt = BigInt(0),
        lastTimestamp: Option[BigInt] = None,
        lastId: Option[String] = None)(swapSelection: SelectionBuilder[Swap, A]): SelectionBuilder[RootQuery, Seq[A]] = {
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
      val sortDirection  = Argument("orderDirection", "desc", "")
      val swapsField     = Field[Pair, List[A]](
        "swaps",
        ListOf(Obj(swapSelection)),
        arguments = List(filterArg, orderByArg, sortDirection)
      )
      Field("pair", Obj(swapsField), arguments = List(Argument("id", pairId, "String!")))
    }
  }

  def priceWMEMO(): Request[Either[CalibanClientError, Option[(String, BigDecimal)]], Any] = {
    val query = Queries.pairData(PairAddress.SUSHI_WMEMO_MIM)(Pair.name ~ Pair.token1Price)
    val req   = query.toRequest(Endpoints.SUSHISWAP).mapResponseRight(round)
    req
  }

  def wMemoSwapsRequest(
      since: Instant,
      minSwap: BigInt,
      lastTimestamp: Option[BigInt] = None,
      lastId: Option[String] = None): Request[Either[CalibanClientError, Seq[SwapDetails]], Any] = {
    Queries
      .pairSwapsSinceInstant(PairAddress.SUSHI_WMEMO_MIM, since, minSwap, lastTimestamp, lastId)(SwapDetails.DETAILS_MAPPED)
      .toRequest(Endpoints.SUSHISWAP)
  }

  private def round(res: Option[(String, BigDecimal)]): Option[(String, BigDecimal)] = {
    res.map { case (label, price) => (label, price.setScale(3, RoundingMode.HALF_UP)) }
  }
}
