package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.Shape
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.{NumberShapeAsync2Node, StringShapeAsync2Node}

import scala.concurrent.Future

object Async20TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {

  override def id: String = "AsyncTypeFacetsCompletionPlugin"

  val dialect: Dialect = AsyncApi20Dialect.dialect

  override def stringShapeNode: NodeMapping = StringShapeAsync2Node.Obj

  override def numberShapeNode: NodeMapping = NumberShapeAsync2Node.Obj

  override def integerShapeNode: NodeMapping = NumberShapeAsync2Node.Obj

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
