package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.apicontract.client.scala.model.domain.Payload
import amf.apicontract.internal.annotations.DefaultPayload
import amf.apicontract.internal.metamodel.domain.PayloadModel
import amf.core.client.scala.model.domain.AmfElement
import amf.core.internal.metamodel.Field
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}
class PayloadSymbolBuilder(override val element: Payload)(implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[Payload] {
  override def ignoreFields: List[Field] = super.ignoreFields :+ PayloadModel.Schema

  override protected val children: List[DocumentSymbol] =
    super.children ++
      Option(element.schema)
        .flatMap(s => ctx.factory.builderFor(s))
        .map(bs => bs.build().flatMap(_.children))
        .getOrElse(Nil)

  override protected val optionName: Option[String] =
    element.mediaType.option().orElse(element.name.option()).orElse(Some("payload"))

  override def build(): Seq[DocumentSymbol] = {
    if (element.annotations.contains(classOf[DefaultPayload])) children
    else super.build()
  }
}

object PayloadSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[Payload] {
  override def getType: Class[_ <: AmfElement] = classOf[Payload]

  override val supportedIri: String = PayloadModel.`type`.head.iri()

  override def construct(element: Payload)(implicit ctx: StructureContext): Option[SymbolBuilder[Payload]] =
    Some(new PayloadSymbolBuilder(element))
}
