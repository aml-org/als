package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import amf.plugins.document.vocabularies.model.domain.PublicNodeMapping
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.common.NodeBranchBuilder
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit

import scala.concurrent.Future

class AMLRootDeclarationsCompletionPlugin(params: CompletionParams, brothers: Set[String])
    extends AMLSuggestionsHelper {

  def extractText(mapping: PublicNodeMapping): (String, String) =
    if (mapping.mappedNode().isNullOrEmpty) (s"${mapping.name().value()}", ": ")
    else (s"${mapping.name().value()}", ":\n  ")

  private def getSuggestions: Seq[(String, String)] =
    params.actualDialect
      .documents()
      .root()
      .declaredNodes()
      .map(extractText)

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .filter(tuple => !brothers.contains(tuple._1)) // TODO: extract filter for all plugins?
        .map(s =>
          new RawSuggestion {
            override def newText: String = s._1

            override def displayText: String = s._1

            override def description: String = s._1

            override def textEdits: Seq[TextEdit] = Seq()

            override def whiteSpacesEnding: String = s._2
        }))
}

object AMLRootDeclarationsCompletionPlugin extends CompletionPlugin {
  override def id = "AMLRootDeclarationsCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    val ast = params.currentBaseUnit match {
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }

    ast
      .map(a => {
        val yPart = NodeBranchBuilder.build(a, params.position)
        if (yPart.isAtRoot && // entry and root
            yPart.isKey && !params.fieldEntry
              .exists(_.value.value
                .position()
                .exists(li => li.contains(params.position))))
          new AMLRootDeclarationsCompletionPlugin(
            params,
            yPart.brothersKeys
          ).resolve()
        else Future.successful(Seq())
      })
      .getOrElse(Future.successful(Seq()))
  }
}
