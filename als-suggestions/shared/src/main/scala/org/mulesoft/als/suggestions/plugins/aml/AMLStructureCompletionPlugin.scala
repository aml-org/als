package org.mulesoft.als.suggestions.plugins.aml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import amf.core.model.domain.DomainElement
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstance}
import amf.plugins.document.vocabularies.model.domain
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{AmfUtils, YamlUtils}
import org.mulesoft.als.suggestions.DialectRegistry
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

  // TODO: remove or separate indentation from ALS
  //  If not removed, clean up and use AST
  //  In case of maintaining it inside plugins, extract method to common
  def getIndentation: String =
    params.currentBaseUnit.raw
      .flatMap(text => {
        val left = text.substring(0, params.position.offset(text))
        val line = left.substring(left.lastIndexOf("\n")).stripPrefix("\n")
        val first = line.headOption match {
          case Some(c) if c == ' ' || c == '\t' => Some(c)
          case _                                => None
        }
        first.map(f => {
          val spaces = line.substring(0, line.takeWhile(_ == f).length)
          if (f == '\t') s"$spaces\t"
          else s"$spaces  "
        })
      })
      .getOrElse("  ")

  private def getSuggestions: Seq[(String, String)] = {
    val dialect: Dialect =
      params.currentBaseUnit.sourceVendor.flatMap(v => DialectRegistry.get(v.name).headOption) match {
        case Some(d) => d
        case _ =>
          params.currentBaseUnit match {
            case d: DialectInstance =>
              AMLPlugin.registry
                .dialectFor(d)
                .getOrElse(throw new Exception(s"No Dialect for ${params.currentBaseUnit.id} found"))
          }
      }

    val maybeMapping: Option[DomainElement] =
      AmfUtils
        .getFieldEntryByPosition(params.currentBaseUnit, Position(params.position.line + 1, params.position.column))
        .flatMap(fe => getDialectNode(dialect, fe)) // maybe worth to keep calculated as or instead of params.node?

    maybeMapping match {
      case Some(nm: domain.NodeMapping) => nm.propertiesMapping().map(extractText(_, getIndentation))
      case _                            => Nil
    }
  }

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
      case d: Document => d.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
      case bu          => bu.annotations.find(classOf[SourceAST]).map(_.ast)
    }
    val amfPosition = Position(params.position.line + 1, params.position.column)
    if (YamlUtils.isKey(ast, amfPosition))
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
  }
}
