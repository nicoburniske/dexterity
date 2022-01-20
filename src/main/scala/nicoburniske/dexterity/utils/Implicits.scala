package nicoburniske.dexterity.utils

import caliban.client.CalibanClientError.DecodingError
import caliban.client.ScalarDecoder
import caliban.client.__Value.{__NumberValue, __StringValue}

import scala.util.Try

object Implicits {
  implicit val bigIntDecoder: ScalarDecoder[BigInt] = {
    case __NumberValue(value) =>
      Try(value.toBigIntExact).toEither.left
        .map(
          ex =>
            DecodingError(s"Can't build a BigInt from input $value", Some(ex))
        )
        .flatMap {
          case None =>
            Left(DecodingError(s"Can't build a BigInt from input $value"))
          case Some(v) => Right(v)
        }
    case __StringValue(value) => // added this case here to handle strings
      Try(BigInt(value)).toEither.left.map(
        ex => DecodingError(s"Can't build a BigInt from input $value", Some(ex))
      )
    case other => Left(DecodingError(s"Can't build a BigInt from input $other"))
  }

  implicit val bigDecimalDecoder: ScalarDecoder[BigDecimal] = {
    case __NumberValue(value) => Right(value)
    case __StringValue(value) => // added this case here to handle strings
      Try(BigDecimal(value)).toEither.left.map(
        ex =>
          DecodingError(s"Can't build a BigDecimal from input $value", Some(ex))
      )
    case other =>
      Left(DecodingError(s"Can't build a BigDecimal from input $other"))
  }
}
