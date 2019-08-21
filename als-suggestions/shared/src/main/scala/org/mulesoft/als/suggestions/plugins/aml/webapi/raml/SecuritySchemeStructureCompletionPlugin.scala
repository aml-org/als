package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.domain.webapi.models.security.SecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SecuritySchemeStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySchemeStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case s: SecurityScheme if request.yPartBranch.isKeyDescendanceOf("describedBy") =>
          SecuritySchemesDialect.DescribedBy.propertiesRaw(request.indentation)
        case s: SecurityScheme if request.fieldEntry.isEmpty && request.yPartBranch.isKey =>
          val suggestions =
            new AMLStructureCompletionsPlugin(
              SecuritySchemesDialect.SecurityScheme.propertiesMapping(),
              request.indentation).resolve(SecuritySchemesDialect.SecurityScheme.meta.`type`.head.iri())
          if (s.`type`
                .option()
                .exists(t => Seq("OAuth 1.0", "OAuth 2.0").contains(t)))
            suggestions :+ RawSuggestion("settings", request.indentation, isAKey = true, "security")
          else suggestions

        case _ => Nil
      }
    }
  }
}
