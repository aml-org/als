package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders

import amf.apicontract.client.scala.model.domain.security.Settings
import amf.apicontract.internal.metamodel.domain.security.SettingsModel
import amf.core.client.scala.model.domain.AmfElement
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

class RamlSecuritySchemesSettingsSymbolBuilder(override val element: Settings)(
    override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[Settings] {

  override protected val children: List[DocumentSymbol] = Nil

  override protected val optionName: Option[String] = Some("settings")
}

object RamlSecuritySchemesSettingsSymbolBuilder extends AmfObjectSimpleBuilderCompanion[Settings] {
  override def getType: Class[_ <: AmfElement] = classOf[Settings]

  override val supportedIri: String = SettingsModel.`type`.head.iri()

  override def construct(element: Settings)(implicit ctx: StructureContext): Option[SymbolBuilder[Settings]] =
    Some(new RamlSecuritySchemesSettingsSymbolBuilder(element))
}
