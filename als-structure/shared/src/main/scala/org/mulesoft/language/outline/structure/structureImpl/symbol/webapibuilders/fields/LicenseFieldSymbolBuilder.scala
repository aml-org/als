package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ObjectFieldTypeSymbolBuilderCompanion,
  SingleObjectFieldSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  FieldTypeSymbolBuilder,
  IriFieldSymbolBuilderCompanion
}

class LicenseFieldSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val ctx: StructureContext
) extends SingleObjectFieldSymbolBuilder {
  override protected val name: String = "License"
}

object LicenseFieldSymbolBuilderCompanion
    extends ObjectFieldTypeSymbolBuilderCompanion
    with IriFieldSymbolBuilderCompanion {

  override val supportedIri: String = WebApiModel.License.value.iri()

  override def construct(element: FieldEntry, value: AmfObject)(implicit
      ctx: StructureContext
  ): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new LicenseFieldSymbolBuilder(value, element))
}
