package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.metamodel.domain.NodeMappingModel.NodeTypeMapping
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{External, NodeMappable, PropertyMapping}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object NamespaceForTermCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "NamespaceForTermCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (applies(request))
      Future {
        externals(request.baseUnit).map(RawSuggestion(_, isAKey = false))
      } else emptySuggestion
  }

  private def applies(request: AmlCompletionRequest) = {
    request.amfObject match {
      case _: NodeMappable =>
        request.yPartBranch.parentEntryIs("classTerm")
      case _: PropertyMapping =>
        request.yPartBranch.parentEntryIs("propertyTerm") ||
          request.yPartBranch.parentEntryIs("mapTermKey") ||
          request.yPartBranch.parentEntryIs("mapTermValue")
      case _ => false
    }
  }
  private def externals(bu: BaseUnit): Seq[String] = {
    bu match {
      case d: Dialect => d.externals.collect({ case e: External => e.alias.option() }).flatten
      case _          => Nil
    }

  }
}
