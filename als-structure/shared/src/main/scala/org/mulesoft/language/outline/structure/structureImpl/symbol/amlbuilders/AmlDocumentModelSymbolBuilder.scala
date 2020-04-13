package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.core.model.domain.AmfElement
import amf.plugins.document.vocabularies.metamodel.domain.DocumentsModelModel
import amf.plugins.document.vocabularies.model.domain.DocumentsModel
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.AmfObjSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  ElementSymbolBuilder,
  ElementSymbolBuilderCompanion
}

case class AmlDocumentModelSymbolBuilder(element: DocumentsModel)(override implicit val factory: BuilderFactory)
    extends AmfObjSymbolBuilder[DocumentsModel] {
  override protected val name: String                          = "documents"
  override protected val selectionRange: Option[PositionRange] = None
}

object AmlDocumentModelSymbolBuilderCompanion extends ElementSymbolBuilderCompanion {
  override type T = DocumentsModel

  override def getType: Class[_ <: AmfElement] = classOf[DocumentsModel]

  override val supportedIri: String = DocumentsModelModel.`type`.head.iri()

  override def construct(element: DocumentsModel)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[_ <: DocumentsModel]] =
    Some(AmlDocumentModelSymbolBuilder(element))
}
