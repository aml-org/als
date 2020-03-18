package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import amf.plugins.domain.webapi.models.security.SecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLStructureCompletionsPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfmanager.dialect.webapi.raml.raml10.Raml10SecuritySchemesDialect

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Raml10SecuritySchemeStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySchemeStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case s: SecurityScheme if request.yPartBranch.isKeyDescendantOf("describedBy") =>
          Raml10SecuritySchemesDialect.DescribedBy.propertiesRaw()
        case s: SecurityScheme if request.fieldEntry.isEmpty && request.yPartBranch.isKey =>
          val suggestions =
            new AMLStructureCompletionsPlugin(Raml10SecuritySchemesDialect.SecurityScheme.propertiesMapping())
              .resolve(Raml10SecuritySchemesDialect.SecurityScheme.nodetypeMapping.value())
          if (s.`type`
                .option()
                .exists(t => Seq("OAuth 1.0", "OAuth 2.0").contains(t)))
            suggestions :+ RawSuggestion.forObject("settings", "security")
          else suggestions

        case _ => Nil
      }
    }
  }
}
