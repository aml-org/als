package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.core.model.domain.AmfArray
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.OasBaseUnitSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.{
  RamlBaseUnitSymbolBuilder,
  RamlSecuritySchemesSettingsSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders._
import org.mulesoft.language.outline.structure.structureImpl.{
  BuilderFactory,
  CompanionList,
  ElementSymbolBuilderCompanion
}

trait AmfBuilderFactory extends BuilderFactory {

  override protected def companion: CompanionList = super.companion ++ List(
    HeadersSymbolBuilder,
    QueryParametersSymbolBuilder,
    QueryStringSymbolBuilder,
    UriParametersSymbolBuilder,
    RequestSymbolBuilders,
    ObjectNodeSymbolBuilder,
    ArrayNodeSymbolBuilder,
    PropertyShapeSymbolBuilder,
    EndPointListBuilder,
    WebApiVersionBuilder,
    WebApiSymbolBuilder,
    CreativeWorkListSymbolBuilder,
    ShapeInheritsSymbolBuilder
  )

  override protected val defaultArrayBuilder = Some((e: AmfArray) => new WebApiArraySymbolBuilder(e))
}

object RamlBuilderFactory extends AmfBuilderFactory {

  override protected def companion: CompanionList = super.companion + RamlSecuritySchemesSettingsSymbolBuilder

  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    RamlBaseUnitSymbolBuilder
}

object OasBuilderFactory extends AmfBuilderFactory {

  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    OasBaseUnitSymbolBuilder
}
