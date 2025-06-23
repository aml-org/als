package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.shapes.client.scala.model.domain.NodeShape
import amf.shapes.internal.domain.metamodel.NodeShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.AMLDiscriminatorObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DiscriminatorObject extends ExceptionPlugin {
  override def id: String = "DiscriminatorObject"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (applies(request)) Future { suggestBody(request.actualDocumentDefinition) }
    else emptySuggestion
  }

  private def suggestBody(d: DocumentDefinition) = AMLDiscriminatorObject.Obj.propertiesRaw(fromDefinition = d)

  override def applies(request: AmlCompletionRequest): Boolean =
    request.amfObject.isInstanceOf[NodeShape] && isInDiscriminator(request)

  private def isInDiscriminator(request: AmlCompletionRequest): Boolean =
    (request.astPartBranch.isKeyDescendantOf("discriminator") && request.fieldEntry.isEmpty) || request.fieldEntry
      .exists(_.field == NodeShapeModel.Discriminator)

}
