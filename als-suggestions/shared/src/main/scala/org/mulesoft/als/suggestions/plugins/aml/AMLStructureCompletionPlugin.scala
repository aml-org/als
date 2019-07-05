package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.YamlUtils.getParents
import org.mulesoft.als.common.{AmfUtils, YamlUtils}
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit
import org.yaml.model.YMapEntry

import scala.concurrent.Future

class AMLStructureCompletions(params: CompletionParams, brothers: Set[String]) extends AMLSuggestionsHelper {

  def extractText(mapping: PropertyMapping, indent: String): (String, String) = {
    val cleanText = mapping.name().value()
    val whiteSpaces =
      if (mapping.literalRange().isNullOrEmpty) s":\n$indent"
      else ": "
    (cleanText, whiteSpaces)
  }

  private def getSuggestions: Seq[(String, String)] =
    params.propertyMappings.map(extractText(_, getIndentation(params.currentBaseUnit, params.position)))

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

object AMLStructureCompletionPlugin extends CompletionPlugin {
  override def id = "AMLStructureCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    val ast = params.currentBaseUnit match {
      case d: Document =>
        d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }
    val amfPosition = params.position.moveLine(1)

    ast
      .map(a => {
        val parents = getParents(a, amfPosition, Seq())
        if (YamlUtils
              .isKey(parents.headOption, amfPosition) && !params.fieldEntry
              .exists(_.value.value
                .position()
                .exists(li => AmfUtils.containsPosition(li, amfPosition, None))))
          new AMLStructureCompletions(
            params,
            ast
              .map(yaml =>
                YamlUtils.getNodeBrothers(yaml, amfPosition).flatMap {
                  case yme: YMapEntry => yme.key.asScalar.map(_.text)
                  case _              => None
              })
              .getOrElse(Nil)
              .toSet
          ).resolve()
        else Future.successful(Seq())
      })
      .getOrElse(Future.successful(Seq()))
  }
}
