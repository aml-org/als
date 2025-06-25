package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.{Payload, Server}
import amf.core.client.scala.model.domain.Shape
import amf.shapes.client.scala.model.domain.AnyShape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.FieldTypeKnowledge
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This plugin is responsible for suggesting only root suggestion from payload type facets for Async
  */
object Async20PayloadCompletionPlugin
    extends AMLCompletionPlugin
    with AsyncMediaTypePluginFinder
    with FieldTypeKnowledge {
  override def id: String = "Async20PayloadCompletionPlugin"


    override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
      val branchStack = request.branchStack
      branchStack.headOption match {
        case Some(p: Payload)
            if request.astPartBranch.isKeyDescendantOf("payload") &&
              !branchStack.exists(_.isInstanceOf[Server]) =>
          request.amfObject match {
            case s: Shape =>
              if (p.schemaMediaType.isNullOrEmpty)
                Future(Async20TypeFacetsCompletionPlugin.resolveShape(s, branchStack, DocumentDefinition(AsyncApi20Dialect())))
              else
                findPluginForMediaType(p)
                  .map {
                    case wf: WebApiTypeFacetsCompletionPlugin =>
                      Future(wf.resolveShape(s, branchStack, DocumentDefinition(AsyncApi20Dialect())))
                    case generic =>
                      generic.resolve(request)
                  }
                  .getOrElse(Future(Async20TypeFacetsCompletionPlugin.resolveShape(s, branchStack, DocumentDefinition(AsyncApi20Dialect()))))
          }
        case _ => emptySuggestion
      }
    }
}
