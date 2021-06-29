package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMappable, UnionNodeMapping}
import amf.aml.internal.metamodel.domain.UnionNodeMappingModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UnionRangeCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "UnionRangeCompletionPlugin"

  def getSuggestions(maybeDialect: Option[Dialect], name: String): Option[Seq[RawSuggestion]] = {
    maybeDialect.map(dialect => {
      dialect.declares
        .flatMap {
          case nm: NodeMappable[_] if nm.name.value() != name => Some(nm.name.value())
          case _                                              => None
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
