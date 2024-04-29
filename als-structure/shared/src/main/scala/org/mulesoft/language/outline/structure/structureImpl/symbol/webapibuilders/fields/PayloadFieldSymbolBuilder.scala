package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.client.scala.model.domain.{Parameter, Payload}
import amf.apicontract.internal.annotations.{BodyParameter, FormBodyParameter}
import amf.apicontract.internal.metamodel.domain.{EndPointModel, ParametersFieldModel}
import amf.core.client.scala.model.domain.{AmfArray, Shape}
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.client.scala.model.domain.NodeShape
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilder,
  ArrayFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ParameterBindingLabelMapper

class PayloadFieldSymbolBuilder(override val element: FieldEntry, override val value: AmfArray)(
    override implicit val ctx: StructureContext
) extends ArrayFieldTypeSymbolBuilder {

  protected def buildForKey(key: String, sons: List[DocumentSymbol]): Option[DocumentSymbol] =
    if (sons.nonEmpty) {
      val r: PositionRange = range.map(PositionRange(_)).getOrElse(sons.head.range)
      Some(
        DocumentSymbol(
          key,
          KindForResultMatcher.kindForField(ParametersFieldModel.QueryParameters),
          r,
          skipLoneChild(sons, key)
        )
      )
    } else None

  private val payloads: Seq[Payload] = value.values.collect({ case p: Payload => p })

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
    buildForKey(
      ParameterBindingLabelMapper.toLabel("formData"),
      formData
        .flatMap(f => ctx.factory.builderFor(f))
        .flatMap(_.build())
        .map(_.copy(kind = KindForResultMatcher.getKind(Parameter())))
        .toList
    )

  protected def bodySymbols: Option[DocumentSymbol] =
    buildForKey(
      ParameterBindingLabelMapper.toLabel("body"),
      body.flatMap(b => ctx.factory.builderFor(b)).flatMap(_.build()).toList
    )

  protected val payloadsLabel = "Payloads"
  protected def payloadSymbols: Option[DocumentSymbol] =
    buildForKey(payloadsLabel, realPayloads.flatMap(p => ctx.factory.builderFor(p)).flatMap(_.build()).toList)

  override def build(): Seq[DocumentSymbol] = formDataSymbols.toSeq ++ bodySymbols ++ payloadSymbols

  override protected val optionName: Option[String] = None
}

object PayloadFieldSymbolCompanion extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override val supportedIri: String =
    EndPointModel.Payloads.value.iri() // same than RequestModel.Payload Apicontract.Payload

  override def construct(element: FieldEntry, value: AmfArray)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new PayloadFieldSymbolBuilder(element, value))
}
