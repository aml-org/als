package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields

import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.FieldTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  DefaultObjectTypeSymbolBuilder,
  SingleObjectFieldSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DefaultMappedScalarTypeSymbolBuilderCompanion

object DefaultWebApiScalarTypeSymbolBuilderCompanion extends DefaultMappedScalarTypeSymbolBuilderCompanion {
  override protected val mapName = Map(
    WebApiModel.Version -> "version"
  )
}

class DefaultWebApiObjectTypeSymbolBuilder(override val value: AmfObject, override val element: FieldEntry)(
    override implicit val ctx: StructureContext)
    extends SingleObjectFieldSymbolBuilder {

  // Linceese and contant are named domain eelemento, so i cannot apply this hack
  private val mapName = Map(
    WebApiModel.License  -> "License",
    WebApiModel.Provider -> "Contact"
  )

  override protected val name: String = mapName.getOrElse(element.field, element.field.value.name)
}

object DefaultWebApiObjectTypeSymbolBuilderCompanion extends DefaultObjectTypeSymbolBuilder {
  override def construct(element: FieldEntry, value: AmfObject)(
      implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfObject]] =
    Some(new DefaultWebApiObjectTypeSymbolBuilder(value, element))
}
