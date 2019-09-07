package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.AmfElement
import amf.core.parser.{Range => AmfRange}
import amf.plugins.domain.webapi.metamodel.security.SettingsModel
import amf.plugins.domain.webapi.models.security.Settings
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.AmfObjSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  ElementSymbolBuilder,
  ElementSymbolBuilderCompanion
}

class RamlSecuritySchemesSettingsSymbolBuilder(override val element: Settings)(
    override implicit val factory: BuilderFactory)
    extends AmfObjSymbolBuilder[Settings] {
  override protected val name: String = "settings"
  override protected val selectionRange: Option[PositionRange] =
    element.annotations.find(classOf[LexicalInformation]).map(_.range).map(PositionRange.apply)
}

object RamlSecuritySchemesSettingsSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = Settings

  override def getType: Class[_ <: AmfElement] = classOf[Settings]

  override val supportedIri: String = SettingsModel.`type`.head.iri()

  override def construct(element: Settings)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[Settings]] =
    Some(new RamlSecuritySchemesSettingsSymbolBuilder(element))
}
