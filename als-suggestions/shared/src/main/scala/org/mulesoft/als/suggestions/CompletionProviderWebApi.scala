package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.implementation.{CompletionRequest, LocationKindDetectTool}
import org.mulesoft.als.suggestions.interfaces.{Suggestion => SuggestionInterface, _}
import org.mulesoft.high.level.interfaces.{IASTUnit, IParseResult}
import org.mulesoft.lexer.{AstToken, InputRange}
import org.mulesoft.positioning.{YamlLocation, YamlSearch}
import org.yaml.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProviderWebApi extends CompletionProvider {
  var _config: ICompletionConfig = _

  var _pluginsRegistry: ICompletionPluginsRegistry =
    CompletionPluginsRegistry.instance

  def withConfig(cfg: ICompletionConfig): CompletionProviderWebApi = {
    _config = cfg
    this
  }

  override def suggest: Future[Seq[SuggestionInterface]] = suggest(true)

  def suggest(filterByPrefix: Boolean): Future[Seq[SuggestionInterface]] = {
    val request = composeRequest
    fulfillRequest(request).map(result => {
      if (filterByPrefix) filter(result, request)
      else result
    })
  }

  def composeRequest: ICompletionRequest = {
    var prefix: String        = ""
    var position: Int         = -1
    var currentIndent: String = ""
    var indentCount: Int      = 0

    _config.editorStateProvider match {
      case Some(esp) =>
        position = esp.getOffset

        currentIndent = CompletionProviderWebApi.getCurrentIndent(esp)

        indentCount = CompletionProviderWebApi.getCurrentIndentCount(esp)

        prefix = _config.astProvider match {
          case Some(ap) =>
            val positionDto = Position(position, esp.getText)
            val positionAmf = Position(positionDto.line + 1, positionDto.column)

            val result = CompletionProviderWebApi.getAstNode(ap) match {
              case (ast1, Some(ast2)) =>
                val result = CompletionProviderWebApi.getInnerNode(Option(ast2), positionAmf)
                ast1 match {
                  case Some(ast) =>
                    ast.sourceInfo.content match {
                      case Some(content) =>
                        if (content.length - position - result.length - 1 >= 0 && content
                              .charAt(position - result.length - 1) == '!')
                          s"!$result"
                        else result
                      case _ => result
                    }
                  case _ => result
                }
              case _ =>
                esp.getText.substring(0, position)
            }
            result
              .substring(CompletionProviderWebApi.getIdxForPrefix(result) + 1)
              .dropWhile(_ == ' ')
        }
      case _ => throw new Error("Editor state provider must be supplied")
    }

    val text   = _config.editorStateProvider.get.getText
    val isJSON = text.trim.startsWith("{")
    val result = CompletionRequest(LocationKindDetectTool.determineCompletionKind(text, position),
                                   prefix,
                                   position,
                                   _config,
                                   currentIndent,
                                   indentCount)

    _config.astProvider match {
      case Some(ap) =>
        val ast                                      = CompletionProviderWebApi.getAstNode(ap)
        val astNode                                  = ast._1
        val astUnit: Option[IASTUnit]                = astNode.map(_.astUnit)
        val positionsMapper                          = astUnit.map(_.positionsMapper)
        var yamlLocation: Option[YamlLocation]       = None
        var actualYamlLocation: Option[YamlLocation] = None
        if (positionsMapper.isDefined) {
          yamlLocation = ast._2.map(YamlLocation(_, positionsMapper.get))
          actualYamlLocation = ast._2.map(
            YamlSearch
              .getLocation(position, _, positionsMapper.get, List(), isJSON))
        }
        result
          .withAstNode(ast._1)
          .withYamlLocation(yamlLocation)
          .withActualYamlLocation(actualYamlLocation)
      case None =>
    }
    result
  }

  def fulfillRequest(request: ICompletionRequest): Future[Seq[SuggestionInterface]] = {
    val filteredPlugins = _pluginsRegistry.plugins.filter(plugin => {
      plugin.isApplicable(request)
    })
    Future
      .sequence(filteredPlugins.map(_.suggest(request)))
      .map(responses =>
        responses.flatMap(r => {

          val styler = SuggestionStyler.adjustedSuggestions(
            StylerParams(
              request.config.astProvider.exists(_.syntax == Syntax.YAML),
              r.kind == LocationKind.KEY_COMPLETION,
              r.noColon,
              _config.originalContent.get,
              Position(request.position, _config.originalContent.get)
            ),
            _
          )
          styler(r.suggestions)
        }))
  }

  def filter(suggestions: Seq[SuggestionInterface], request: ICompletionRequest): Seq[SuggestionInterface] =
    suggestions.filter(s => {
      val prefix = s.prefix.toLowerCase
      if (prefix.isEmpty || prefix == ":" || prefix == "/") true
      else s.displayText.toLowerCase.startsWith(prefix)
    })
}

object CompletionProviderWebApi {
  def apply(): CompletionProviderWebApi = new CompletionProviderWebApi()

  def getIdxForPrefix(text: String): Int =
    text
      .lastIndexOf("\n")
      .max(text.lastIndexOf("\""))
      .max(text.lastIndexOf("{"))
      .max(text.lastIndexOf("["))
      .max(text.lastIndexOf(":") - 1) // In case of "key:*" the ':' marks that there must be a space added

  def containsPosition(range: InputRange, position: Position): Boolean =
    Position(range.lineFrom, range.columnFrom) <= position &&
      Position(range.lineTo, range.columnTo) >= position

  def positionWithOffset(position: Position, offset: Position): Position = {
    val line = position.line - offset.line
    val col = line match {
      case 0 => position.column - offset.column
      case _ => position.column
    }
    if (line < 0 || col < 0) Position(0, 0)
    else Position(line, col)
  }

  def getPrefixByPosition(sNode: String, position: Position): String =
    if (position.line > 0)
      getPrefixByPosition(sNode.substring(sNode.indexOf('\n')), Position(position.line - 1, position.column))
    else {
      var line = sNode.split('\n').head
      line = line.substring(0, position.column.min(line.length - 1))
      line.substring(getIdxForPrefix(line) + 1)
    }

  def getInnerNode(part: Option[YPart], position: Position): String =
    part match {
      case Some(yNon: YNonContent) => getInnerNode(yNon, position)
      case Some(yTag: YTag)        => getLastInnerNode(yTag, position)
      case Some(yPart: YPart)      => getInnerNode(yPart, position)
      case _                       => ""
    }

  def getInnerNode(yNon: YNonContent, position: Position): String =
    yNon.tokens.find(y => containsPosition(y.range, position)) match {
      case Some(ast) => getLastInnerNode(ast, position)
      case _ =>
        getInnerNode(yNon.children.find(y => containsPosition(y.range, position)), position)
    }

  def getInnerNode(yPart: YPart, position: Position): String =
    getInnerNode(yPart.children.find(y => containsPosition(y.range, position)), position)

  def getLastInnerNode(ast: AstToken, position: Position): String = {
    val result = ast.text
      .substring(0, positionWithOffset(position, Position(ast.range.lineFrom, ast.range.columnFrom)).column)
    result.substring(0.max(result.lastIndexOf('\"') + 1))
  }

  def getLastInnerNode(tag: YTag, position: Position): String = {
    val result = tag.text
      .substring(0, positionWithOffset(position, Position(tag.range.lineFrom, tag.range.columnFrom)).column)
    result.substring(0.max(result.lastIndexOf('\"') + 1))
  }

  def getCurrentIndent(content: IEditorStateProvider): String = {
    (getIndentation(content.getText, content.getOffset),
     getIndentation(content.getText, getLineStart(content.getText, content.getOffset))) match {
      case (currentIndentation, previousIndentation)
          if (!currentIndentation.contains(" ") && currentIndentation.contains("\t")) ||
            (!previousIndentation.contains(" ") && previousIndentation.contains("\t")) =>
        "\t"
      case _ => "  "
    }
  }

  def getLineStart(text: String, offset: Int): Int =
    text.lastIndexWhere(c => { c == '\r' || c == '\n' }, offset - 1)

  def getIndentation(text: String, offset: Int): String = {
    val lineStartPosition: Int = getLineStart(text, offset)

    if (lineStartPosition <= 0) ""
    else {
      val line = text.substring(lineStartPosition + 1, offset)

      val trimmed = line.trim

      if (trimmed.length == 0)
        line
      else {
        val end = line.indexOf(trimmed)
        line.substring(0, end)
      }
    }
  }

  def getCurrentIndentCount(content: IEditorStateProvider): Int =
    0.max(getIndentation(content.getText, content.getOffset).length / getCurrentIndent(content).length)

  def getAstNode(astProvider: IASTProvider): (Option[IParseResult], Option[YPart]) = {
    val astNodeOpt = astProvider.getSelectedNode
    val yamlNodes = astNodeOpt match {
      case Some(n) => n.sourceInfo.yamlSources
      case _       => Seq()
    }
    (astNodeOpt, yamlNodes.headOption)
  }
}
