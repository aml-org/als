package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.metamodel.security.SecuritySchemeModel
import amf.plugins.domain.webapi.models.security.SecurityRequirement
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.mulesoft.als.suggestions.{ArrayRange, RawSuggestion}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async2SecuredByCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "SecuredByCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingSecuredBy(request)) {
        val original = getSecurityNames(request.prefix, request.declarationProvider)
        if (request.yPartBranch.isKey)
          original.map(r => r.copy(options = r.options.copy(isKey = true, rangeKind = ArrayRange)))
        else original.map(r => r.copy(options = r.options.copy(isKey = false, rangeKind = ArrayRange)))

      } else Nil
    }
  }

  private def isWritingSecuredBy(request: AmlCompletionRequest): Boolean = {
    request.amfObject match {
      case _: SecurityRequirement =>
        request.fieldEntry.isEmpty && request.yPartBranch.isDescendanceOf("security")
      case _ => false
    }
  }

  private def getSecurityNames(prefix: String, dp: DeclarationProvider): Seq[RawSuggestion] =
    new AMLRamlStyleDeclarationsReferences(Seq(SecuritySchemeModel.`type`.head.iri()), prefix, dp, None)
      .resolve()
}
