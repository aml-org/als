package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import amf.core.model.domain.AmfArray
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.oasbuilders.{
  Oas20BaseUnitSymbolBuilder,
  Oas20WebApiSymbolBuilder,
  Oas30BaseUnitSymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.{
  RamlBaseUnitSymbolBuilder,
  RamlSecuritySchemesSettingsSymbolBuilder,
  RamlWebApiSymbolBuilder
}
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
    ExampleSymbolBuilders,
    DomainExtensionSymbolBuilder,
    RequestSymbolBuilders,
    ObjectNodeSymbolBuilder,
    PropertyShapeSymbolBuilder,
    EndPointListBuilder,
    WebApiVersionBuilder,
    ShapeInheritsSymbolBuilder,
    OperationSymbolBuilderCompanion,
    ParameterSymbolBuilderCompanion,
    PayloadSymbolBuilderCompanion,
    CustomDomainPropertySymbolBuilderCompanion
  )

  override protected val defaultArrayBuilder = Some((e: AmfArray) => new WebApiArraySymbolBuilder(e))
}