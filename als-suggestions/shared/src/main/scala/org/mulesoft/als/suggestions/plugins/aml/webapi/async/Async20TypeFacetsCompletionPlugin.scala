package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import amf.apicontract.client.scala.model.domain.{Payload, Server}
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.shapes.client.scala.model.domain.UnionShape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.AvroTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.{AnyShapeAsync2Node, NumberShapeAsync2Node, StringShapeAsync2Node}

import scala.concurrent.Future

object Async20TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin with AsyncMediaTypePluginFinder {

  override def id: String = "AsyncTypeFacetsCompletionPlugin"

  val dialect: Dialect = AsyncApi20Dialect.dialect

  override def stringShapeNode: NodeMapping = StringShapeAsync2Node.Obj

  override def numberShapeNode: NodeMapping = NumberShapeAsync2Node.Obj

  override def integerShapeNode: NodeMapping = NumberShapeAsync2Node.Obj

  override def anyShapeNode: NodeMapping = AnyShapeAsync2Node.Obj

  def propertyShapeNode: Option[NodeMapping] = None

  def declarations: Seq[NodeMapping] =
    dialect.declares.collect({ case n: NodeMapping => n })

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.branchStack.headOption match {
      case Some(_: Payload)                                       => emptySuggestion
      case _ if params.branchStack.exists(_.isInstanceOf[Server]) => emptySuggestion
      case Some(_: PropertyShape) if params.branchStack.exists(_.isInstanceOf[Payload]) =>
        params.branchStack.collectFirst { case p: Payload => p }.flatMap(findPluginForMediaType)
          .map(_.resolve(params))
          .getOrElse(super.resolve(params))
      case Some(_: Shape) if params.branchStack.exists(_.isInstanceOf[Payload]) =>
        // hack for avro maps and other non conventional payloads (see if we can handle this another way or improve this code as a whole)
        params.branchStack.collectFirst { case p: Payload => p }.flatMap(findPluginForMediaType)
          .map {
            case AvroTypeFacetsCompletionPlugin =>
              AvroTypeFacetsCompletionPlugin.resolve(params)
            case _ => emptySuggestion
          }
          .getOrElse(super.resolve(params))
      case _ => super.resolve(params)
    }
  }

  override protected def defaults(s: Shape): Seq[RawSuggestion] = Seq()
}
