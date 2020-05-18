package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.core.model.domain.AmfElement
import amf.plugins.document.vocabularies.metamodel.domain.DocumentsModelModel
import amf.plugins.document.vocabularies.model.domain.DocumentsModel
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

  override def construct(element: DocumentsModel)(
      implicit ctx: StructureContext): Option[SymbolBuilder[DocumentsModel]] =
    Some(AmlDocumentModelSymbolBuilder(element))
}
