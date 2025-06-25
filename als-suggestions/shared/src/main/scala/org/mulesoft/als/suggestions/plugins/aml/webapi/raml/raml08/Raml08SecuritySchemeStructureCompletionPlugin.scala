package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.apicontract.client.scala.model.domain.security.SecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLStructureCompletionsPlugin, _}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08SecuritySchemesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Raml08SecuritySchemeStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySchemeStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case s: SecurityScheme if request.astPartBranch.isKeyDescendantOf("describedBy") =>
          Raml08SecuritySchemesDialect.DescribedBy.propertiesRaw(fromDefinition = request.actualDocumentDefinition)
        case s: SecurityScheme if request.fieldEntry.isEmpty && request.astPartBranch.isKey =>
          val suggestions =
            new AMLStructureCompletionsPlugin(
              Raml08SecuritySchemesDialect.SecurityScheme.propertiesMapping(),
              request.actualDocumentDefinition
            )
              .resolve(
                Raml08SecuritySchemesDialect.SecurityScheme.meta.`type`.head
                  .iri()
              ) :+
              RawSuggestion.forObject("settings", "security")
          suggestions
        case _ => Nil
      }
    }
  }
}
