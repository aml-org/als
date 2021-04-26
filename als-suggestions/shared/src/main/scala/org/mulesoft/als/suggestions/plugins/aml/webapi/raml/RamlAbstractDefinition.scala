package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.RamlProfile
import amf.core.annotations.SourceAST
import amf.core.errorhandling.UnhandledErrorHandler
import amf.core.model.document.Fragment
import amf.core.model.domain.DomainElement
import amf.core.model.domain.templates.AbstractDeclaration
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel}
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import amf.plugins.domain.webapi.resolution.ExtendsHelper
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, AmlCompletionRequestBuilder}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLRefTagCompletionPlugin, AMLRootDeclarationsCompletionPlugin}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler
import org.yaml.model.{YMap, YMapEntry, YNode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlAbstractDefinition extends AMLCompletionPlugin {
  override def id: String = "RamlAbstractDefinition"

  private val ignoredPlugins: Set[AMLCompletionPlugin] =
    Set(AMLRefTagCompletionPlugin, AMLRootDeclarationsCompletionPlugin)

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    val info = if (params.yPartBranch.isIncludeTagValue) None else elementInfo(params)

    info
      .map { info =>
        if (params.baseUnit.isInstanceOf[Fragment])
          info.element.fields.filter(t => !(t._1 == EndPointModel.Path || t._1 == OperationModel.Method))
        val newRequest =
          AmlCompletionRequestBuilder.forElement(info.element,
                                                 info.original,
                                                 params.declarationProvider.filterLocal(info.name, info.iri),
                                                 params,
                                                 ignoredPlugins)
        newRequest.completionsPluginHandler
          .pluginSuggestions(newRequest)
          .map(seq => {
            if (params.branchStack.headOption.exists(_.isInstanceOf[AbstractDeclaration]) && !params.baseUnit
                  .isInstanceOf[Fragment] && params.yPartBranch.isKey)
              seq ++ Seq(RawSuggestion.forKey("usage", "docs", mandatory = false))
            else seq
          })
      }
      .getOrElse(Future.successful(Nil))
  }

  private case class ElementInfo(element: DomainElement, original: DomainElement, name: String, iri: String)

  private def findAbstractDeclaration(params: AmlCompletionRequest) = {
    params.amfObject match {
      case a: AbstractDeclaration => Some(a)
      case _                      => params.branchStack.collectFirst({ case a: AbstractDeclaration => a })
    }
  }

  private def elementInfo(params: AmlCompletionRequest): Option[ElementInfo] = {
    findAbstractDeclaration(params) match {
      case Some(r: ResourceType) =>
        val resolved = getSourceEntry(r, "resourceType").fold(
          r.asEndpoint(params.baseUnit, errorHandler = LocalIgnoreErrorHandler))(e => {
          r.entryAsEndpoint(params.baseUnit,
                            node = r.dataNode,
                            entry = e,
                            errorHandler = LocalIgnoreErrorHandler,
                            annotations = r.annotations)
        })
        Some(ElementInfo(resolved, r, r.name.value(), r.metaURIs.head))

      case Some(t: Trait) =>
        val resolved =
          getSourceEntry(t, "trait").fold(t.asOperation(params.baseUnit))(e => {
            val extendsHelper = ExtendsHelper(RamlProfile, keepEditingInfo = false, UnhandledErrorHandler)
            extendsHelper.parseOperation(params.baseUnit, t.name.option().getOrElse(""), id, e)
          })
        Some(ElementInfo(resolved, t, t.name.value(), t.metaURIs.head))
      case _ => None
    }
  }

  private def getSourceEntry(a: AbstractDeclaration, defaultName: String) =
    a.annotations.find(classOf[SourceAST]).map(_.ast) match {
      case Some(m: YMap) =>
        Some(YMapEntry(YNode(a.name.option().getOrElse(defaultName)), m))
      case Some(entry: YMapEntry) => Some(entry)
      case _                      => None
    }

}
