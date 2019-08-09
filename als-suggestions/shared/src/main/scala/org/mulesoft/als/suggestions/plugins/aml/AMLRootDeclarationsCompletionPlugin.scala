package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.document.{DeclaresModel, Document, Module}
import amf.plugins.document.vocabularies.metamodel.domain.DocumentsModelModel
import amf.plugins.document.vocabularies.model.document.{DialectInstance, DialectInstanceLibrary}
import amf.plugins.document.vocabularies.model.domain.PublicNodeMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AMLRootDeclarationsCompletionPlugin(params: AmlCompletionRequest) extends AMLSuggestionsHelper {

  def extractText(mapping: PublicNodeMapping): (String, String) =
    if (mapping.mappedNode().isNullOrEmpty) (s"${mapping.name().value()}", "")
    else (s"${mapping.name().value()}", "\n  ")

  private def getSuggestions: Seq[(String, String)] =
    params.baseUnit match {
      case d: DeclaresModel if params.actualDialect.documents().declarationsPath().option().isDefined =>
        params.actualDialect.documents().declarationsPath().option().map(v => (v, "\n  ")).toSeq
      case d: DeclaresModel =>
        params.baseUnit match {
          case _: DialectInstance | _: Document =>
            params.actualDialect
              .documents()
              .root()
              .declaredNodes()
              .map(extractText)
          case _: DialectInstanceLibrary | _: Module =>
            params.actualDialect
              .documents()
              .library()
              .declaredNodes()
              .map(extractText)
          case _ => Nil
        }
      case _ => Nil
    }

  def usesSuggestion(): Option[(String, String)] =
    params.actualDialect.documents().fields.getValueAsOption(DocumentsModelModel.Library).map(_ => ("uses", "\n  "))

  def resolve(): Future[Seq[RawSuggestion]] =
    Future {
      (getSuggestions ++ usesSuggestion())
        .map(s => RawSuggestion(s._1, s._2, isAKey = true))
    }
}

object AMLRootDeclarationsCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLRootDeclarationsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    if (params.yPartBranch.isAtRoot && params.yPartBranch.isKey)
      new AMLRootDeclarationsCompletionPlugin(
        params
      ).resolve()
    else emptySuggestion
  }
}
