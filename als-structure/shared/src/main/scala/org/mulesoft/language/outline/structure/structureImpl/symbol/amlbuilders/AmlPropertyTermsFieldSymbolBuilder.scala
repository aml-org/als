package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.core.metamodel.document.DocumentModel
import amf.core.model.domain.{AmfArray, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.domain.{ClassTerm, DatatypePropertyTerm, ObjectPropertyTerm}
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.ArrayFieldTypeSymbolBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DeclaresFieldSymbolBuilder

class AmlPropertyTermsFieldSymbolBuilder(override val value: AmfArray, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends DeclaresFieldSymbolBuilder(value, element) {

  override def declarationName(obj: AmfObject): String = obj match {
    case _: ClassTerm            => "ClassTerms"
    case _: ObjectPropertyTerm   => "PropertyTerms"
    case _: DatatypePropertyTerm => "PropertyTerms"
  }
}

object AmlPropertyTermsFieldSymbolBuilder
    extends ArrayFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {
  override def construct(element: FieldEntry, value: AmfArray)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfArray]] =
    Some(new AmlPropertyTermsFieldSymbolBuilder(value, element))

  override val supportedIri: String = DocumentModel.Declares.value.iri()
}
