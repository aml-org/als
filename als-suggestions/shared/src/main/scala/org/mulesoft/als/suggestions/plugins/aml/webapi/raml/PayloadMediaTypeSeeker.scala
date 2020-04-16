package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.metamodel.domain.ShapeModel
import amf.plugins.domain.webapi.models.{Operation, Payload}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

trait PayloadMediaTypeSeeker {

  protected def insideMediaType(request: AmlCompletionRequest): Boolean =
    request.branchStack.headOption match {
      case Some(p: Payload) =>
        p.schema.fields
          .filter(f => f._1 != ShapeModel.Name)
          .fields()
          .isEmpty && p.mediaType
          .option()
          .nonEmpty
      case _ => false
    }

  protected def isWritingKEYMediaType(request: AmlCompletionRequest): Boolean =
    request.yPartBranch.isKey &&
      (request.branchStack.headOption match {
        case Some(p: Payload) =>
          p.schema.fields
            .filter(f => f._1 != ShapeModel.Name)
            .fields()
            .isEmpty && (p.mediaType
            .option()
            .isEmpty || inMediaType(request.yPartBranch))
        case Some(_: Operation) =>
          request.yPartBranch.isKey && request.yPartBranch.isKeyDescendantOf("body")
        case _ => false
      })

  // todo : replace hack when amf keeps lexical information over media type field in payload
  protected def inMediaType(yPartBranch: YPartBranch): Boolean =
    yPartBranch.stringValue.contains('/') && yPartBranch.isKeyDescendantOf("body")
}
