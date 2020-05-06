package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import org.mulesoft.language.outline.structure.structureImpl.{BuilderFactory, StructureContext}
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.{
  ContactFieldSymbolBuilderCompanion,
  DefaultWebApiArrayFieldTypeSymbolBuilderCompanion,
  DefaultWebApiScalarTypeSymbolBuilderCompanion,
  LicenseFieldSymbolBuilderCompanion,
  OperationsArrayFieldBuilderCompanion,
  PayloadsArrayFieldBuilderCompanion,
  WebApiEncodesFieldSymbolBuilderCompanion
}

trait AmfBuilderFactory extends BuilderFactory {

  override protected def companion: FieldCompanionList =
    super.companion +
      DefaultWebApiArrayFieldTypeSymbolBuilderCompanion +
      DefaultWebApiScalarTypeSymbolBuilderCompanion +
      HeadersSymbolBuilder +
      QueryParametersSymbolBuilder +
      QueryStringSymbolBuilder +
      UriParametersSymbolBuilder +
      ExampleSymbolBuilders +
      DomainExtensionSymbolBuilder +
      ObjectNodeSymbolBuilder +
      PropertyShapeSymbolBuilder +
      OperationSymbolBuilderCompanion +
      OperationsArrayFieldBuilderCompanion +
      ParameterSymbolBuilderCompanion +
      PayloadSymbolBuilderCompanion +
      CustomDomainPropertySymbolBuilderCompanion +
      PayloadsArrayFieldBuilderCompanion +
      EndPointFieldBuilderCompanion +
      WebApiEncodesFieldSymbolBuilderCompanion +
      LicenseFieldSymbolBuilderCompanion +
      ContactFieldSymbolBuilderCompanion
}
