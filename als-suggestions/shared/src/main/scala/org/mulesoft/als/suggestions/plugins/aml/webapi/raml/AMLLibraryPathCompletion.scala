package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.document.BaseUnit
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLPathCompletionPlugin

import scala.concurrent.Future

object AMLLibraryPathCompletion extends AMLCompletionPlugin {
  override def id: String = "AMLLibraryPathCompletion"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {

    if ((request.amfObject
          .isInstanceOf[BaseUnit] || isEncodes(request.amfObject, request.actualDialect)) && request.yPartBranch
          .isInBranchOf("uses") && request.yPartBranch.isValue) {
      AMLPathCompletionPlugin.resolveInclusion(
        request.baseUnit.location().getOrElse(""),
        request.environment,
        request.platform,
        request.directoryResolver,
        request.prefix,
        request.rootUri,
        request.amfInstance
      )
    } else emptySuggestion
  }
}
