package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.plugins.domain.webapi.models.security.SecurityScheme
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLStructureCompletionsPlugin
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Raml08SecuritySchemeStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuritySchemeStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case s: SecurityScheme if request.yPartBranch.isKeyDescendanceOf("describedBy") =>
          Raml08SecuritySchemesDialect.DescribedBy.propertiesRaw(request.indentation)
        case s: SecurityScheme if request.fieldEntry.isEmpty && request.yPartBranch.isKey =>
          val suggestions =
            new AMLStructureCompletionsPlugin(Raml08SecuritySchemesDialect.SecurityScheme.propertiesMapping(),
                                              request.indentation,
                                              request.yPartBranch,
                                              request.amfObject)
              .resolve(Raml08SecuritySchemesDialect.SecurityScheme.meta.`type`.head
                .iri()) :+
              RawSuggestion("settings", request.indentation, isAKey = true, "security")
          suggestions
        case _ => Nil
      }
    }
  }
}
