package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.aml.client.scala.model.domain.DocumentsModel
import amf.aml.internal.metamodel.domain.DocumentsModelModel
import amf.core.client.scala.model.domain.AmfElement
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}

case class AmlDocumentModelSymbolBuilder(element: DocumentsModel)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[DocumentsModel] {
  override protected def optionName: Option[String] = Some("documents")
}

object AmlDocumentModelSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[DocumentsModel] {

  override def getType: Class[_ <: AmfElement] = classOf[DocumentsModel]

  override val supportedIri: String = DocumentsModelModel.`type`.head.iri()

  override def construct(element: DocumentsModel)(implicit
      ctx: StructureContext
  ): Option[SymbolBuilder[DocumentsModel]] =
    Some(AmlDocumentModelSymbolBuilder(element))
}
