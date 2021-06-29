package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.aml.client.scala.model.document.Dialect
import amf.shapes.internal.domain.metamodel.NodeShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaForOasWrapper
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.oas.oas3.JsonSchemas
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect

import scala.concurrent.Future

object Oas30TypeFacetsCompletionPlugin extends OasTypeFacetsCompletionPlugin {
  override def jsonSchemaObj: JsonSchemaForOasWrapper = JsonSchemas

  override def dialect: Dialect = OAS30Dialect.dialect

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (Oas30ExceptionPlugins.applyAny(params) || params.fieldEntry.exists(
          _.field == NodeShapeModel.DiscriminatorMapping)) emptySuggestion
    else super.resolve(params)
  }

}
