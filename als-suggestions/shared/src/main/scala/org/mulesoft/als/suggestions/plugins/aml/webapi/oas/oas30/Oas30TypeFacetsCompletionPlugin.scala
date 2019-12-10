package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasTypeFacetsCompletionPlugin
import org.mulesoft.amfmanager.dialect.webapi.oas.{JsonSchemaForOasWrapper, Oas30DialectWrapper}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Oas30TypeFacetsCompletionPlugin extends OasTypeFacetsCompletionPlugin {
  override def jsonSchameObj: JsonSchemaForOasWrapper = Oas30DialectWrapper.JsonSchemas

  override def dialect: Dialect = Oas30DialectWrapper.dialect

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (VariableValueParam.applies(params)) Future { VariableValueParam.suggest() } else super.resolve(params)
  }
}
