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
    RequestSymbolBuilders,
    ObjectNodeSymbolBuilder,
    PropertyShapeSymbolBuilder,
    EndPointListBuilder,
    WebApiVersionBuilder,
    ShapeInheritsSymbolBuilder,
    OperationSymbolBuilderCompanion,
    ParameterSymbolBuilderCompanion,
    PayloadSymbolBuilderCompanion
  )

  override protected val defaultArrayBuilder = Some((e: AmfArray) => new WebApiArraySymbolBuilder(e))
}

object RamlBuilderFactory extends AmfBuilderFactory {

  override protected def companion: CompanionList =
    super.companion + RamlSecuritySchemesSettingsSymbolBuilder + RamlWebApiSymbolBuilder

  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    RamlBaseUnitSymbolBuilder
}

object Oas20BuilderFactory extends AmfBuilderFactory {

  override protected def companion: CompanionList = super.companion + Oas20WebApiSymbolBuilder
  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    Oas20BaseUnitSymbolBuilder
}

object Oas30BuilderFactory extends AmfBuilderFactory {

  override def baseUnitBuilder: ElementSymbolBuilderCompanion =
    Oas30BaseUnitSymbolBuilder

  override def companion: CompanionList = super.companion + WebApiSymbolBuilder
}
