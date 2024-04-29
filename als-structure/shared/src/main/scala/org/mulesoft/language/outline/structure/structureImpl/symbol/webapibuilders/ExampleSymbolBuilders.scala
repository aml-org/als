package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.client.scala.model.domain.AmfElement
import amf.shapes.client.scala.model.domain.Example
import amf.shapes.internal.domain.metamodel.ExampleModel
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  AmfObjectSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class ExampleSymbolBuilders(override val element: Example)(override implicit val ctx: StructureContext)
    extends AmfObjectSymbolBuilder[Example] {
  override protected val children: List[DocumentSymbol] = Nil

  override protected val optionName: Option[String] = element.name.option().orElse(element.mediaType.option())
}

object ExampleSymbolBuilders extends AmfObjectSimpleBuilderCompanion[Example] {
  override def getType: Class[_ <: AmfElement] = classOf[Example]

  override val supportedIri: String = ExampleModel.`type`.head.iri()

  override def construct(element: Example)(implicit ctx: StructureContext): Option[SymbolBuilder[Example]] =
    Some(new ExampleSymbolBuilders(element))
}
