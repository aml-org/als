package org.mulesoft.als.suggestions.plugins.aml

import amf.plugins.document.vocabularies.model.document.{DialectInstance, DialectInstanceLibrary}
import amf.plugins.document.vocabularies.model.domain.PublicNodeMapping
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit

import scala.concurrent.Future

class AMLRootDeclarationsCompletionPlugin(params: CompletionParams) extends AMLSuggestionsHelper {

  def extractText(mapping: PublicNodeMapping): (String, String) =
    if (mapping.mappedNode().isNullOrEmpty) (s"${mapping.name().value()}", "")
    else (s"${mapping.name().value()}", "\n  ")

  private def getSuggestions: Seq[(String, String)] =
    params.currentBaseUnit match {
      case _: DialectInstance =>
        params.actualDialect
          .documents()
          .root()
          .declaredNodes()
          .map(extractText)
      case _: DialectInstanceLibrary =>
        params.actualDialect
          .documents()
          .library()
          .declaredNodes()
          .map(extractText)
      case _ => Nil
    }

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .map(s =>
          new RawSuggestion {
            override def newText: String = s._1

            override def displayText: String = s._1

            override def description: String = s._1

            override def textEdits: Seq[TextEdit] = Seq()

            override def whiteSpacesEnding: String = s._2

            override def isKey: Boolean = true
        }))
}

object AMLRootDeclarationsCompletionPlugin extends CompletionPlugin {
  override def id = "AMLRootDeclarationsCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    if (params.yPartBranch.isAtRoot && params.yPartBranch.isKey)
      new AMLRootDeclarationsCompletionPlugin(
        params
      ).resolve()
    else Future.successful(Seq())
  }
}
