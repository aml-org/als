package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.{AmfArray, AmfObject, Shape}
import amf.core.parser.FieldEntry
import amf.plugins.document.webapi.annotations.{BodyParameter, FormBodyParameter}
import amf.plugins.domain.shapes.models.NodeShape
import amf.plugins.domain.webapi.metamodel.{EndPointModel, ParametersFieldModel}
import amf.plugins.domain.webapi.models.{Parameter, Payload}
import org.mulesoft.language.outline.structure.structureImpl._

class PayloadFieldSymbolBuilder(override val element: FieldEntry, override val value: AmfArray)(
    override implicit val factory: BuilderFactory)
    extends ArrayFieldTypeSymbolBuilder {

  private def buildForKey(key: String, sons: List[DocumentSymbol]): Option[DocumentSymbol] = {
    if (sons.nonEmpty)
      Some(
        DocumentSymbol(
          key,
          KindForResultMatcher.kindForField(ParametersFieldModel.QueryParameters),
          deprecated = false,
          range,
          range,
          sons
        ))
    else None
  }

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
      "Form Data Parameters",
      formData
        .flatMap(f => factory.builderFor(f))
        .flatMap(_.build())
        .map(_.copy(kind = KindForResultMatcher.getKind(Parameter())))
        .toList
    )

  protected def bodySymbols: Option[DocumentSymbol] =
    buildForKey("Body Parameters", body.flatMap(b => factory.builderFor(b)).flatMap(_.build()).toList)

  protected def payloadSymbols: Option[DocumentSymbol] =
    buildForKey("payloads", realPayloads.flatMap(p => factory.builderFor(p)).flatMap(_.build()).toList)

  override def build(): Seq[DocumentSymbol] = formDataSymbols.toSeq ++ bodySymbols ++ payloadSymbols

}

object PayloadFieldSymbolCompanion extends ArrayFieldTypeSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion {
  override val supportedIri
    : String = EndPointModel.Payloads.value.iri() // same than RequestModel.Payload Apicontract.Payload

  override def construct(element: FieldEntry, value: AmfArray)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new PayloadFieldSymbolBuilder(element, value))
}
