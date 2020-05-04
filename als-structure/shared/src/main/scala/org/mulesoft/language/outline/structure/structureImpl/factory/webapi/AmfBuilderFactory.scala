package org.mulesoft.language.outline.structure.structureImpl.factory.webapi

import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders._
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.custom.array.builder.PayloadsArrayFieldBuilderCompanion
import org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders.fields.{
  DefaultArrayFieldTypeSymbolBuilderCompanion,
  DefaultWebApiScalarTypeSymbolBuilderCompanion
}

trait AmfBuilderFactory extends BuilderFactory {

  override protected def companion: FieldCompanionList =
    super.companion +
      DefaultArrayFieldTypeSymbolBuilderCompanion +
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
      ParameterSymbolBuilderCompanion +
      PayloadSymbolBuilderCompanion +
      CustomDomainPropertySymbolBuilderCompanion +
      PayloadsArrayFieldBuilderCompanion
}
