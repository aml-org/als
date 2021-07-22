package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.core.benchmark.ExecutionLog.executionContext
import amf.plugins.document.vocabularies.metamodel.domain.UnionNodeMappingModel
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMappable, NodeMapping, UnionNodeMapping}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object UnionRangeCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "UnionRangeCompletionPlugin"

  def getSuggestions(maybeDialect: Option[Dialect], name: String): Option[Seq[RawSuggestion]] = {
    maybeDialect.map(dialect => {
      dialect.declares
        .flatMap {
          case nm: NodeMappable if nm.name.value() != name => Some(nm.name.value())
          case _                                           => None
        }
        .map(RawSuggestion(_, isAKey = false))
    })
  }

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    extractName(request)
      .flatMap(name => getSuggestions(request.branchStack.collectFirst({ case d: Dialect => d }), name))
      .getOrElse(Seq.empty)
  }

  def extractName(request: AmlCompletionRequest): Option[String] = request.amfObject match {
    case u: UnionNodeMapping if request.fieldEntry.exists(_.field == UnionNodeMappingModel.ObjectRange) =>
      Some(u.name.value())
    case _ => None
  }

}
