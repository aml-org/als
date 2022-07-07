package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLPathCompletionPlugin

import scala.concurrent.Future

object AMLLibraryPathCompletion extends AMLCompletionPlugin {
  override def id: String = "AMLLibraryPathCompletion"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {

    if (
      (request.amfObject
        .isInstanceOf[BaseUnit] ||
        isEncodes(request.amfObject, request.actualDialect, request.branchStack)) && matchesAST(request)
    ) {
      AMLPathCompletionPlugin.resolveInclusion(
        request.baseUnit.location().getOrElse(""),
        request.directoryResolver,
        request.prefix,
        request.rootUri,
        request.alsConfigurationState
      )
    } else emptySuggestion
  }

  private def matchesAST(request: AmlCompletionRequest) = {
    request.astPartBranch
      .isInBranchOf("uses") && request.astPartBranch.isValue
  }
}
