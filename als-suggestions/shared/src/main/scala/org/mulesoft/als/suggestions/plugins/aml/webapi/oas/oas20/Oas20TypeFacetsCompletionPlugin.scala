package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.Oas20ParameterStructure.resolveParameterStructure
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaForOasWrapper
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas2.JsonSchemas
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect

import scala.concurrent.Future

object Oas20TypeFacetsCompletionPlugin extends OasTypeFacetsCompletionPlugin {
  override def jsonSchemaObj: JsonSchemaForOasWrapper = JsonSchemas

  override def dialect: Dialect = OAS20Dialect.dialect

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val parameterSuggestions = params.branchStack.headOption
      .map(o => resolveParameterStructure(params.yPartBranch, o, None))
      .getOrElse(Nil)
    if (parameterSuggestions.isEmpty) super.resolve(params)
    else Future.successful(parameterSuggestions)
  }
}
