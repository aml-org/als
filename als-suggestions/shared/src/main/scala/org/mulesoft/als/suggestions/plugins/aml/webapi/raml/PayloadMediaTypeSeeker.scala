package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.apicontract.client.scala.model.domain.{Operation, Payload, Request}
import amf.core.client.scala.model.domain.Shape
import amf.core.internal.metamodel.domain.ShapeModel
import org.mulesoft.als.common.ASTPartBranch
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

  protected def isWritingKeyMediaType(request: AmlCompletionRequest): Boolean =
    request.astPartBranch.isKey &&
      (request.branchStack.headOption match {
        case Some(_: Shape)
            if request.branchStack.collectFirst({ case p: Payload if p.mediaType.option().isEmpty => p }).isDefined =>
          inMediaType(request.astPartBranch)
        case Some(p: Payload) =>
          p.mediaType
            .option()
            .isEmpty && inMediaType(request.astPartBranch)
        case Some(_: Operation) => inMediaType(request.astPartBranch)
        case Some(_: Request)   => inMediaType(request.astPartBranch)
        case _                  => false
      })

  // todo : replace hack when amf keeps lexical information over media type field in payload
  protected def inMediaType(astPartBranch: ASTPartBranch): Boolean = astPartBranch.isKeyDescendantOf("body")
}
