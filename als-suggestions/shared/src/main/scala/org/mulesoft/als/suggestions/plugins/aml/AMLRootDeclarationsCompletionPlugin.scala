package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.document.DeclaresModel
import amf.plugins.document.vocabularies.model.document.{DialectInstance, DialectInstanceLibrary}
import amf.plugins.document.vocabularies.model.domain.PublicNodeMapping
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

class AMLRootDeclarationsCompletionPlugin(params: AMLCompletionParams) extends AMLSuggestionsHelper {

  def extractText(mapping: PublicNodeMapping): (String, String) =
    if (mapping.mappedNode().isNullOrEmpty) (s"${mapping.name().value()}", "")
    else (s"${mapping.name().value()}", "\n  ")

  private def getSuggestions: Seq[(String, String)] =
    params.baseUnit match {
      case d: DeclaresModel if params.dialect.documents().declarationsPath().option().isDefined =>
        params.dialect.documents().declarationsPath().option().map(v => (v, "\n  ")).toSeq
      case d: DeclaresModel =>
        params.baseUnit match {
          case _: DialectInstance =>
            params.dialect
              .documents()
              .root()
              .declaredNodes()
              .map(extractText)
          case _: DialectInstanceLibrary =>
            params.dialect
              .documents()
              .library()
              .declaredNodes()
              .map(extractText)
          case _ => Nil
        }
      case _ => Nil
    }

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .map(s => RawSuggestion(s._1, s._2, isAKey = true)))
}

object AMLRootDeclarationsCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLRootDeclarationsCompletionPlugin"

  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] = {
    if (params.yPartBranch.isAtRoot && params.yPartBranch.isKey)
      new AMLRootDeclarationsCompletionPlugin(
        params
      ).resolve()
    else Future.successful(Seq())
  }
}
