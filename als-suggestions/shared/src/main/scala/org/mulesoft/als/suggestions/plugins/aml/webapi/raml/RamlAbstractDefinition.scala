package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.templates.AbstractDeclaration
import amf.core.model.domain.{DataNode, DomainElement}
import amf.core.parser.ErrorHandler
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, AmlCompletionRequestBuilder}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{CompletionsPluginHandler, RawSuggestion}

import scala.concurrent.Future

object RamlAbstractDefinition extends AMLCompletionPlugin {
  override def id: String = "RamlAbstractDefinition"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val info = if (params.amfObject.isInstanceOf[DataNode]) elementInfo(params) else None

    info
      .map { info =>
        val newRequest =
          AmlCompletionRequestBuilder.forElement(info.element,
                                                 params.declarationProvider.filterLocal(info.name, info.iri),
                                                 params)
        CompletionsPluginHandler.pluginSuggestions(newRequest)
      }
      .getOrElse(Future.successful(Nil))
  }

  private case class ElementInfo(element: DomainElement, name: String, iri: String)

  private def elementInfo(params: AmlCompletionRequest): Option[ElementInfo] = {
    params.branchStack.collectFirst({ case a: AbstractDeclaration => a }) match {
      case Some(r: ResourceType) =>
        Some(
          ElementInfo(r.asEndpoint(params.baseUnit, errorHandler = new LocalErrorHandler()),
                      r.name.value(),
                      r.meta.`type`.head.iri()))

      case Some(t: Trait) =>
        Some(ElementInfo(t.asOperation(params.baseUnit), t.name.value(), t.meta.`type`.head.iri()))
      case _ => None
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
