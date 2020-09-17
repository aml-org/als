package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.models.Server
import amf.plugins.domain.webapi.models.bindings.{ChannelBinding, MessageBinding, OperationBinding, ServerBinding}
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
    if (Async2ExceptionPlugins.applyAny(request)) emptySuggestion else super.resolve(request)

  private def isBindingAtRef(params: AmlCompletionRequest) = {
    params.amfObject match {
      case _: ServerBinding | _: MessageBinding | _: ChannelBinding | _: OperationBinding =>
        params.yPartBranch.isKeyDescendantOf("bindings")
      case _: ScalarShape => !params.branchStack.exists(_.isInstanceOf[Server])
      case _              => true
    }
  }
//
//  private def isOperationTrait(params:AmlCompletionRequest) = {
//    params.amfObject match {
//      case o:OperationTrait if params.yPartBranch.isKey && o.linkTarget.exists(_.isInstanceOf[ErrorOperationTrait])
//    }
//  }
  override def isExceptionCase(branch: YPartBranch): Boolean = isInsideRequired(branch)
}
