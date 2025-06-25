package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import amf.apicontract.client.scala.model.domain.security.SecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLStructureCompletionsPlugin, _}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10SecuritySchemesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Raml10SecuritySchemeStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySchemeStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case s: SecurityScheme if request.astPartBranch.isKeyDescendantOf("describedBy") =>
          Raml10SecuritySchemesDialect.DescribedBy.propertiesRaw(fromDefinition = request.actualDocumentDefinition)
        case s: SecurityScheme if request.fieldEntry.isEmpty && request.astPartBranch.isKey =>
          val suggestions =
            new AMLStructureCompletionsPlugin(
              Raml10SecuritySchemesDialect.SecurityScheme.propertiesMapping(),
              request.actualDocumentDefinition
            )
              .resolve(Raml10SecuritySchemesDialect.SecurityScheme.nodetypeMapping.value())
          if (
            s.`type`
              .option()
              .exists(t => Seq("OAuth 1.0", "OAuth 2.0").contains(t))
          )
            suggestions :+ RawSuggestion.forObject("settings", "security")
          else suggestions

        case _ => Nil
      }
    }
  }
}
