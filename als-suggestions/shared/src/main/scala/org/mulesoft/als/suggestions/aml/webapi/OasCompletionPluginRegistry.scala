package org.mulesoft.als.suggestions.aml.webapi

import amf.dialects.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas._
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.{
  Oas20ParameterStructure,
  Oas20StructurePlugin,
  Oas20TypeFacetsCompletionPlugin
}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30._
import org.mulesoft.als.suggestions.plugins.aml.webapi.{
  ObjectExamplePropertiesCompletionPlugin,
  SecuredByCompletionPlugin,
  WebApiKnownValueCompletionPlugin
}
import org.mulesoft.als.suggestions.{AMLBaseCompletionPlugins, CompletionsPluginHandler}

trait OasBaseCompletionRegistry {
  val common: Seq[AMLCompletionPlugin] = AMLBaseCompletionPlugins.all :+
    OASRequiredObjectCompletionPlugin :+
    SecuredByCompletionPlugin :+
    ExampleMediaType :+
    OasStructurePlugin :+
    ParameterReferenceCompletionPlugin :+
    OASRefTag :+
    OperationTags :+
    ObjectExamplePropertiesCompletionPlugin :+
    OasNumberShapeFormatValues :+
    QueryParamNamesFromPath :+
    WebApiKnownValueCompletionPlugin
}

object Oas20CompletionPluginRegistry extends OasBaseCompletionRegistry {

  private val all = common :+ Oas20ParameterStructure :+ Oas20StructurePlugin :+ Oas20TypeFacetsCompletionPlugin

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, OAS20Dialect().id)
}

object Oas30CompletionPluginRegistry extends OasBaseCompletionRegistry {

  private val all = common :+ Oas30ParameterStructure :+ EncodingPropertyName :+ Oas30TypeFacetsCompletionPlugin :+ OasUrlTemplateParam :+ OAS30RefTag :+ RefToParameters :+ DiscriminatorFacet :+ VariableValueParam :+ DiscriminatorObject :+ DiscriminatorMappingValue :+ FlowNames :+ OAS30EnumCompletionPlugin :+ RuntimeExpressionsCompletionPlugin

  def init(): Unit =
    CompletionsPluginHandler.registerPlugins(all, OAS30Dialect().id)
}
