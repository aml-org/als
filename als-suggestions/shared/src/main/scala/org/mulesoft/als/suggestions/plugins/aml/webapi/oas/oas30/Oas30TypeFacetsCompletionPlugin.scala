package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.shapes.metamodel.NodeShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasTypeFacetsCompletionPlugin
import org.mulesoft.amfmanager.dialect.webapi.oas.{JsonSchemaForOasWrapper, Oas30DialectWrapper}

import scala.concurrent.Future

object Oas30TypeFacetsCompletionPlugin extends OasTypeFacetsCompletionPlugin {
  override def jsonSchameObj: JsonSchemaForOasWrapper = Oas30DialectWrapper.JsonSchemas

  override def dialect: Dialect = Oas30DialectWrapper.dialect

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (Oas30ExceptionPlugins.applyAny(params) || params.fieldEntry.exists(
          _.field == NodeShapeModel.DiscriminatorMapping)) emptySuggestion
    else super.resolve(params)
  }

}
