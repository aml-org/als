package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.AMLRefTagCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.IsInsideRequired

import scala.concurrent.Future

object AsyncApi20RefTag extends AMLRefTagCompletionPlugin with IsInsideRequired {

  // hack for bindings, has the K: will be a dynamic name. Also, when the refactor of syaml is done, is method should still works (as we will at an empty map?)
  override protected def isObjectDeclarable(params: AmlCompletionRequest): Boolean =
    super.isObjectDeclarable(params)

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (Async2ExceptionPlugins.applyAny(request)) emptySuggestion
    else if (isPayloadRef(request))
      Future.successful(refSuggestion) // hack for RAML types which would result in $ref not showing
    else super.resolve(request)

  private def isPayloadRef(request: AmlCompletionRequest): Boolean =
    request.branchStack.headOption.exists(_.isInstanceOf[Payload]) && isJsonKey(request)

  override def isExceptionCase(branch: YPartBranch): Boolean = isInsideRequired(branch)
}
