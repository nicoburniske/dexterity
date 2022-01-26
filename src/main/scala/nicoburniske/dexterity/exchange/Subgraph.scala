package nicoburniske.dexterity.exchange

object Subgraph {

  sealed trait Endpoint {
    def url: String
    def name: String = getClass.getSimpleName.dropRight(1)
    def https: String = s"https://$url"
    def wss: String   = s"wss://$url"
  }

  object Arbitrum extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/arbitrum-exchange"
  }

  object Avax extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/avalanche-exchange"
  }

  object Bsc extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/bsc-exchange"
  }

  object Celo extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/celo-exchange"
  }

  object Ethereum extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/exchange"
  }

  object Fantom extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/fantom-exchange"
  }

  object Matic extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/matic-exchange"
  }

  object XDai extends Endpoint {
    val url = "api.thegraph.com/subgraphs/name/sushiswap/xdai-exchange"
  }
}
