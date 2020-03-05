package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.Field
import amf.core.model.domain.{AmfArray, AmfObject, Shape}
import amf.core.parser.Value
import amf.plugins.document.webapi.annotations.{BodyParameter, FormBodyParameter}
import amf.plugins.domain.shapes.models.NodeShape
import amf.plugins.domain.webapi.metamodel.{ParametersFieldModel, RequestModel}
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, PositionRange}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, KindForResultMatcher, SymbolKind}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.AmfObjSymbolBuilder
import org.mulesoft.amfintegration.ParserRangeImplicits._
abstract class ParamPayloadDecomposeSymbolBuilders[T <: AmfObject](protected val payloadField: Field)
    extends AmfObjSymbolBuilder[T] {

  override protected val selectionRange: Option[PositionRange] =
    element.annotations.find(classOf[LexicalInformation]).map(_.range).map(PositionRange.apply)
  override def ignoreFields: List[Field] = super.ignoreFields :+ payloadField

  private val payloadValue: Option[Value] = element.fields.getValueAsOption(payloadField)
  private val payloadRange =
    payloadValue.flatMap(_.annotations.find(classOf[LexicalInformation])).map(r => r.range.toPositionRange)
  private val payloads: Seq[Payload] =
    payloadValue.collect({ case Value(AmfArray(values: Seq[Payload], _), _) => values }).getOrElse(Nil)

  private val (formData, body, realPayloads) = {
    val (formBody, others): (Seq[Payload], Seq[Payload]) =
      payloads.partition(_.annotations.contains(classOf[FormBodyParameter]))
    val (body, p) = others.partition(_.annotations.contains(classOf[BodyParameter]))
    (formBody.flatMap(decomposeFormData), body, p)
  }

  private def decomposeFormData(p: Payload): Seq[Shape] = {
    p.schema match {
      case n: NodeShape => n.properties.map(_.range)
      case _            => Nil
    }
  }

  protected def formDataSymbols: Option[DocumentSymbol] =
    buildForKey("Form Data Parameters", formData.flatMap(factory.builderForElement).flatMap(_.build()).toList)

  protected def bodySymbols: Option[DocumentSymbol] =
    buildForKey("Body Parameters", body.flatMap(factory.builderForElement).flatMap(_.build()).toList)

  protected def payloadSymbols: Option[DocumentSymbol] =
    buildForKey("payloads", realPayloads.flatMap(factory.builderForElement).flatMap(_.build()).toList)

  override def children: List[DocumentSymbol] =
    super.children ++ formDataSymbols ++ bodySymbols ++ payloadSymbols

  private def buildForKey(key: String, sons: List[DocumentSymbol]): Option[DocumentSymbol] = {
    if (sons.nonEmpty)
      Some(
        DocumentSymbol(
          key,
          KindForResultMatcher.kindForField(ParametersFieldModel.QueryParameters),
          deprecated = false,
          payloadRange.getOrElse(EmptyPositionRange),
          payloadRange.orElse(range).getOrElse(EmptyPositionRange),
          sons
        ))
    else None
  }

}
