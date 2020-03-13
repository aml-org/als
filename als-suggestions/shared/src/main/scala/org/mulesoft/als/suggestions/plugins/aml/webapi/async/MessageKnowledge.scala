package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.models.Response
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.yaml.model.YMapEntry

object MessageKnowledge {
  def isRootMessageBlock(request: AmlCompletionRequest): Boolean =
    request.amfObject
      .isInstanceOf[Response] && (request.yPartBranch.stack(3) match {
      case entry: YMapEntry =>
        entry.key.asScalar
          .map(_.text)
          .contains("message")
      case _ => false
    })
}
