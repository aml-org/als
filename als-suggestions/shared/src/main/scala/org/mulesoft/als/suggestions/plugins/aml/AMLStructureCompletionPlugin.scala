package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.AmfSonElementFinder._
import org.mulesoft.als.common.NodeBranchBuilder
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit

import scala.concurrent.Future

class AMLStructureCompletions(params: CompletionParams, brothers: Set[String]) extends AMLSuggestionsHelper {

  private def extractText(mapping: PropertyMapping, indent: String): (String, String) = {
    val cleanText = mapping.name().value()
    val whiteSpaces =
      if (mapping.literalRange().isNullOrEmpty) s":\n$indent"
      else ": "
    (cleanText, whiteSpaces)
  }

  private def startsWithLetter(string: String) = {
    val validSet: Set[Char] = (('a' to 'z') ++ ('A' to 'Z') ++ "\"" ++ "\'").toSet
    if (string.headOption.exists(validSet.contains)) true
    else false
  }

  private def getSuggestions: Seq[(String, String)] =
    params.propertyMappings.map(extractText(_, getIndentation(params.currentBaseUnit, params.position)))

  def resolve(): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions
        .filter(tuple => !brothers.contains(tuple._1)) // TODO: extract filter for all plugins?
        .map(s =>
          new RawSuggestion {
            override def newText: String = if (startsWithLetter(s._1)) s._1 else s""""${s._1}""""

            override def displayText: String = s._1

            override def description: String = s._1

            override def textEdits: Seq[TextEdit] = Seq()

            override def whiteSpacesEnding: String = s._2
        }))
}

object AMLStructureCompletionPlugin extends CompletionPlugin {
  override def id = "AMLStructureCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    val ast = params.currentBaseUnit match {
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }

    ast
      .map(a => {
        val yPart = NodeBranchBuilder.build(a, params.position)
        if (yPart.isKey && !params.fieldEntry
              .exists(_.value.value
                .position()
                .exists(li => li.contains(params.position))))
          new AMLStructureCompletions(
            params,
            yPart.brothersKeys
          ).resolve()
        else Future.successful(Seq())
      })
      .getOrElse(Future.successful(Seq()))
  }
}
