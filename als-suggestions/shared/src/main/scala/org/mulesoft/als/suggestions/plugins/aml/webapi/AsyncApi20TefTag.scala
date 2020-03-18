package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.plugins.domain.webapi.models.bindings.{
  ChannelBinding,
  DynamicBinding,
  MessageBinding,
  OperationBinding,
  ServerBinding
}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.AMLRefTagCompletionPlugin

object AsyncApi20TefTag extends AMLRefTagCompletionPlugin {

  // hack for bindings, has the K: will be a dynamic name. Also, when the refactor of syaml is done, is method should still works (as we will at an empty map?)
  override protected def isObjectDeclarable(params: AmlCompletionRequest): Boolean =
    super.isObjectDeclarable(params) && isBindingAtRef(params)

  private def isBindingAtRef(params: AmlCompletionRequest) = {
    params.amfObject match {
      case _: ServerBinding | _: MessageBinding | _: ChannelBinding | _: OperationBinding =>
        params.yPartBranch.isKeyDescendanceOf("bindings")
      case _ => true
    }
  }
}
