package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.Response
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

object MessageKnowledge {
  def isRootMessageBlock(request: AmlCompletionRequest): Boolean =
    request.amfObject
      .isInstanceOf[Response] && request.yPartBranch.isKeyDescendantOf("message")
}
