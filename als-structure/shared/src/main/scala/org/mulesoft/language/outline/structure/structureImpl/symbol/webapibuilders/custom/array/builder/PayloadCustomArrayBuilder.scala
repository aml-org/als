package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.custom.array.builder

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfArray
import amf.core.parser.{FieldEntry, Value}
import amf.plugins.domain.webapi.metamodel.ResponseModel
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, Position0, PositionRange}
import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, DocumentSymbol, KindForResultMatcher}

case class PayloadCustomArrayBuilder(override implicit val factory: BuilderFactory) extends WebApiCustomArrayBuilder {

  override def applies(fe: FieldEntry): Boolean =
    fe.field == ResponseModel.Payloads && isDefaultPayload(fe.value) // only for response oas payloads.

  // todo: how to avoid calculate twice the payload?
  private def isDefaultPayload(value: Value) = firstPayload(value).isDefined

  private def firstPayload(value: Value): Option[Payload] = {
    value match {
      case Value(AmfArray(single: Seq[Payload], _), _) if single.length == 1 => single.headOption
      case _                                                                 => None
    }
  }

  override def build(fe: FieldEntry): Seq[DocumentSymbol] = {
    val symbols = children(fe)
    firstPayload(fe.value)
      .map { p =>
        val range = p.annotations
          .find(classOf[LexicalInformation])
          .map(le => PositionRange(le.range))
          .getOrElse(EmptyPositionRange)
        val positionRange = symbols.headOption.map(_.selectionRange).getOrElse(EmptyPositionRange)
        List(
          DocumentSymbol(name(fe),
                         KindForResultMatcher.kindForField(fe.field),
                         deprecated = false,
                         range,
                         positionRange,
                         symbols))
      }
      .getOrElse(symbols)
  }
}
