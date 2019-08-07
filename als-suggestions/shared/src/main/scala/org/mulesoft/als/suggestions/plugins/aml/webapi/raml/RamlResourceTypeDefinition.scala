package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.DataNode
import amf.core.parser.ErrorHandler
import amf.plugins.domain.webapi.models.templates.ResourceType
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, AmlCompletionRequestBuilder}
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.{CompletionsPluginHandler, RawSuggestion}

import scala.concurrent.Future

object RamlResourceTypeDefinition extends CompletionPlugin {
  override def id: String = "RamlResourceTypeDefinition"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.branchStack.collectFirst({ case r: ResourceType => r }) match {
      case Some(r) if params.amfObject.isInstanceOf[DataNode] =>
        val endPoint = r.asEndpoint(params.baseUnit, errorHandler = new LocalErrorHandler())
        val newRequest = AmlCompletionRequestBuilder.forElement(
          endPoint,
          params.declarationProvider.filterLocal(r.name.value(), r.meta.`type`.head.iri()),
          params)
        CompletionsPluginHandler.pluginSuggestions(newRequest)
      case _ => Future.successful(Nil)
    }
  }

  sealed class LocalErrorHandler() extends ErrorHandler {
    override def reportConstraint(id: String,
                                  node: String,
                                  property: Option[String],
                                  message: String,
                                  lexical: Option[LexicalInformation],
                                  level: String,
                                  location: Option[String]): Unit = {
      println(
        s"Error in RamlResourceTypeDefinition while trying to generate endpoint.\nMessage: $message\nNode: $node")
    }
  }

}
