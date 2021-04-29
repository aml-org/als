package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.ramlbuilders.RamlSecuritySchemeSettingsFieldSymbolBuilderCompanion

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
      ContactFieldSymbolBuilderCompanion +
      ContentTypeIgnoredFieldBuilderCompanion +
      AcceptsIgnoredFieldBuilderCompanion +
      RequestSymbolBuilderCompanion +
      RequestArrayFieldSymbolBuilderCompanion +
      ShapeInheritsArrayFieldBuilderCompanion +
      ExampleArrayFieldCompanion +
      TagsArrayFieldSymbolBuilderCompanion +
      ProtocolsArrayFieldBuilderCompanion +
      AbstractDeclarationDataNodeBuilderCompanion +
      ServerSymbolBuilderCompanion +
      PayloadFieldSymbolCompanion +
      ParameterLinksArrayFieldSymbolBuilderCompanion
}
