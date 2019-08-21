package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.model.document.Fragment
import amf.core.model.domain.templates.AbstractDeclaration
import amf.core.model.domain.{DataNode, DomainElement}
import amf.core.parser.ErrorHandler
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel}
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, AmlCompletionRequestBuilder}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{CompletionsPluginHandler, RawSuggestion}
import org.yaml.model.{YMap, YMapEntry, YNode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlAbstractDefinition extends AMLCompletionPlugin {
  override def id: String = "RamlAbstractDefinition"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val info = if (params.amfObject.isInstanceOf[DataNode]) elementInfo(params) else None

    info
      .map { info =>
        if (params.baseUnit.isInstanceOf[Fragment]) {
          info.element.fields.filter(t => !(t._1 == EndPointModel.Path || t._1 == OperationModel.Method))
        }
        val newRequest =
          AmlCompletionRequestBuilder.forElement(info.element,
                                                 params.declarationProvider.filterLocal(info.name, info.iri),
                                                 params)
        CompletionsPluginHandler
          .pluginSuggestions(newRequest)
          .map(seq => {
            if (params.branchStack.headOption.exists(_.isInstanceOf[AbstractDeclaration]) && params.yPartBranch.isKey)
              seq ++ Seq(RawSuggestion.forKey("usage", "docs"))
            else seq
          })
      }
      .getOrElse(Future.successful(Nil))
  }

  private case class ElementInfo(element: DomainElement, name: String, iri: String)

  private def elementInfo(params: AmlCompletionRequest): Option[ElementInfo] = {
    params.branchStack.collectFirst({ case a: AbstractDeclaration => a }) match {
      case Some(r: ResourceType) =>
        val resolved = getSourceEntry(r, "resourceType").fold(
          r.asEndpoint(params.baseUnit, errorHandler = LocalIgnoreErrorHandler))(e => {
          r.entryAsEndpoint(params.baseUnit,
                            node = r.dataNode,
                            entry = e,
                            errorHandler = LocalIgnoreErrorHandler,
                            annotations = r.annotations)
        })
        Some(ElementInfo(resolved, r.name.value(), r.meta.`type`.head.iri()))

      case Some(t: Trait) =>
        val resolved = getSourceEntry(t, "trait").fold(t.asOperation(params.baseUnit))(e => {
          t.entryAsOperation(params.baseUnit, entry = e, annotations = t.annotations)
        })
        Some(ElementInfo(resolved, t.name.value(), t.meta.`type`.head.iri()))
      case _ => None
    }
  }

  private def getSourceEntry(a: AbstractDeclaration, defaultName: String) = {
    a.annotations.find(classOf[SourceAST]).map(_.ast) match {
      case Some(m: YMap)          => Some(YMapEntry(YNode(a.name.option().getOrElse(defaultName)), m))
      case Some(entry: YMapEntry) => Some(entry)
      case _                      => None
    }
  }

}
