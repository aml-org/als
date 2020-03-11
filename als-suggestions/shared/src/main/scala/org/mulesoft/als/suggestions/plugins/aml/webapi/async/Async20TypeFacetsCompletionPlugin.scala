package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.Shape
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.{AsyncApi20Dialect, AsyncApiObjectNodes}
import amf.plugins.domain.webapi.models.Payload

import scala.concurrent.Future

object Async20TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {

  override def id: String = "AsyncTypeFacetsCompletionPlugin"

  val dialect: Dialect = AsyncApi20Dialect.dialect

  override def stringShapeNode: NodeMapping = AsyncApiObjectNodes.StringShapeNode

  override def numberShapeNode: NodeMapping = AsyncApiObjectNodes.NumberShapeNode

  override def integerShapeNode: NodeMapping = AsyncApiObjectNodes.NumberShapeNode

  def propertyShapeNode: Option[NodeMapping] = None

  def declarations: Seq[NodeMapping] =
    dialect.declares.collect({ case n: NodeMapping => n })

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.branchStack.headOption match {
      case Some(_: Payload) => emptySuggestion
      case _                => super.resolve(params)
    }
  }

  override protected def defaults(s: Shape): Seq[RawSuggestion] = Seq()
}
