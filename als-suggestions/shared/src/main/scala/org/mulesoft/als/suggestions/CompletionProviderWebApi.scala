package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.CompletionProviderWebApi.{getAstNode, getInnerNode}
import org.mulesoft.als.suggestions.implementation.{CompletionRequest, LocationKindDetectTool, Suggestion}
import org.mulesoft.als.suggestions.interfaces.LocationKind._
import org.mulesoft.als.suggestions.interfaces.{Suggestion => SuggestionInterface, _}
import org.mulesoft.high.level.interfaces.{IASTUnit, IParseResult}
import org.mulesoft.lexer.{AstToken, InputRange}
import org.mulesoft.positioning.{PositionsMapper, YamlLocation, YamlSearch}
import org.yaml.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProviderWebApi extends CompletionProvider {
  var _config: ICompletionConfig = _

  var _pluginsRegistry: ICompletionPluginsRegistry = CompletionPluginsRegistry.instance

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

            val result = getAstNode(ap) match {
              case (ast1, Some(ast2)) =>
                val result = getInnerNode(Option(ast2), positionAmf)
                ast1 match {
                  case Some(ast) =>
                    ast.sourceInfo.content match {
                      case Some(content) =>
                        if (content.length - position - result.length - 1 >= 0 && content.charAt(
                              position - result.length - 1) == '!')
                          s"!$result"
                        else result
                      case _ => result
                    }
                  case _ => result
                }
              case _ =>
                esp.getText.substring(0, position)
            }
            result.substring(CompletionProviderWebApi.getIdxForPrefix(result) + 1).dropWhile(_ == ' ')
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
          actualYamlLocation = ast._2.map(YamlSearch.getLocation(position, _, positionsMapper.get, List(), isJSON))
        }
        result.withAstNode(ast._1).withYamlLocation(yamlLocation).withActualYamlLocation(actualYamlLocation)
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
      .map(responses => responses.flatMap(r => adjustedSuggestions(r, request.position)))
  }

  def adjustedSuggestions(response: ICompletionResponse, offset: Int): Seq[SuggestionInterface] = {
    val isKey  = response.kind == LocationKind.KEY_COMPLETION
    val isYAML = response.request.config.astProvider.exists(_.syntax == Syntax.YAML)
    val isJSON = response.request.config.astProvider.exists(_.syntax == Syntax.JSON)

    var hasQuote           = false
    var hasColon           = false
    var hasLine            = false
    var hasKeyClosingQuote = false
    if (_config.originalContent.isDefined) {
      val position = response.request.position
      val pm       = PositionsMapper("original.text").withText(_config.originalContent.get)
      val lineOpt  = pm.lineContainingPosition(position)
      val point    = pm.point(position)
      if (lineOpt.isDefined) {
        hasLine = true
        val tail = lineOpt.get.substring(point.column)
        hasQuote = tail.contains("\"")
        val colonIndex = tail.indexOf(":")
        hasColon = colonIndex >= 0
        if (colonIndex > 0)
          hasKeyClosingQuote = tail.substring(0, colonIndex).trim.endsWith("\"")
        else
          hasKeyClosingQuote = hasQuote
      }
    }

    var result = response.suggestions

    def range(offset: Int, prefix: String): Option[PositionRange] =
      Option(
        PositionRange(Position(offset - prefix.length, _config.originalContent.get),
                      Position(offset, _config.originalContent.get)))

    if (isYAML) {
      if (!response.noColon && isKey) {
        if (!hasLine || !hasColon)
          result = result.map(x => {
            val newText = x.text + ":" + {
              if (x.trailingWhitespace.isEmpty) " " else x.trailingWhitespace
            }
            Suggestion(newText, x.description, x.displayText, x.prefix, range(offset, x.prefix))
              .withCategory(x.category)
          })
      } else if (!isKey)
        result = result.map(
          x =>
            if (x.prefix == ":" && (!x.text.startsWith("\n") || x.text.startsWith("\r\n") || x.text.startsWith(" ")))
              Suggestion(" " + x.text, x.description, x.displayText, x.prefix, range(offset, x.prefix))
                .withCategory(x.category)
            else
              Suggestion({
                if (x.text.endsWith(":")) s"${x.text} " else x.text
              }, x.description, x.displayText, x.prefix, range(offset, x.prefix)).withCategory(x.category))
    } else if (isJSON) {
      var postfix     = ""
      var endingQuote = false
      if (isKey) {
        if (!hasKeyClosingQuote) {
          postfix += "\""
          if (!hasColon && !response.noColon)
            postfix += ":"
        } else if (!hasQuote)
          postfix += "\""
      } else if (!hasQuote) {
        postfix += "\""
        endingQuote = true
      }
      if (postfix.nonEmpty)
        result = result.map(x => {
          val isJSONObject = isJSON && x.text.startsWith("{") && x.text.endsWith("}")
          val newText      = if (!isJSONObject && (!endingQuote || !x.text.endsWith("\""))) x.text + postfix else x.text
          Suggestion(newText, x.description, x.displayText, x.prefix, range(offset, x.prefix)).withCategory(x.category)
        })
    }
    result
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

  def getPrefixByPosition(sNode: String, position: Position): String = {
    if (position.line > 0)
      getPrefixByPosition(sNode.substring(sNode.indexOf('\n')), Position(position.line - 1, position.column))
    else {
      var line = sNode.split('\n').head
      line = line.substring(0, position.column.min(line.length - 1))
      line.substring(getIdxForPrefix(line) + 1)
    }
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
      case _         => getInnerNode(yNon.children.find(y => containsPosition(y.range, position)), position)
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

  def getLineStart(text: String, offset: Int): Int = {
    text.lastIndexWhere(c => { c == '\r' || c == '\n' }, offset - 1)
  }

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

  def getCurrentIndentCount(content: IEditorStateProvider): Int = {
    0.max(getIndentation(content.getText, content.getOffset).length / getCurrentIndent(content).length)
  }

  def getAstNode(astProvider: IASTProvider): (Option[IParseResult], Option[YPart]) = {
    val astNodeOpt = astProvider.getSelectedNode
    val yamlNodes = astNodeOpt match {
      case Some(n) => n.sourceInfo.yamlSources
      case _       => Seq()
    }
    (astNodeOpt, yamlNodes.headOption)
  }

  def prepareYamlContent(text: String, offset: Int): String = {
    val completionKind = LocationKindDetectTool.determineCompletionKind(text, offset)
    val result = completionKind match {
      case KEY_COMPLETION | ANNOTATION_COMPLETION | SEQUENCE_KEY_COPLETION => {
        val newLineIndex = text.indexOf("\n", offset)
        val rightPart =
          if (newLineIndex < 0) text.substring(offset)
          else text.substring(offset, newLineIndex)
        val colonIndex = rightPart.indexOf(":")
        if (colonIndex < 0)
          text.substring(0, offset) + "k: " + text.substring(offset)
        else if (colonIndex == 0) {
          val leftPart       = text.substring(0, offset)
          val leftOfSentence = leftPart.substring(Math.max(leftPart.lastIndexOf('\n'), 0), offset)
          val rightPart      = text.substring(offset)
          val rightOfSentence =
            rightPart.substring(0, Math.min(Math.max(rightPart.indexOf('\n'), 0), rightPart.length))

          val openBrackets = { leftOfSentence + rightOfSentence }.count(_ == '[') - {
            leftOfSentence + rightOfSentence
          }.count(_ == '[')
          text + "k" + " ]" * openBrackets + rightPart
        } else text
      }
      case _ =>
        if (offset == text.length) text + "\n"
        else text
    }
    result
  }

  def prepareJsonContent(textRaw: String, offsetRaw: Int): String = {
    val EOL       = textRaw.find(_ == '\r').map(_ => "\r\n").getOrElse("\n")
    val text      = textRaw.replace(EOL, "\n")
    val offset    = offsetRaw - textRaw.substring(0, offsetRaw).count(_ == '\r')
    val lineStart = 0.max(text.lastIndexOf("\n", 0.max(offset - 1)) + 1)

    var lineEnd = text.indexOf("\n", offset)
    if (lineEnd < 0) lineEnd = text.length
    val line                         = text.substring(lineStart, lineEnd)
    val off                          = offset - lineStart
    val lineTrim                     = line.trim
    val textEnding                   = text.substring(lineEnd + 1).trim
    val hasComplexValueStartSameLine = lineTrim.endsWith("{") || lineTrim.endsWith("[")
    val hasComplexValueSameLine      = hasComplexValueStartSameLine || lineTrim.endsWith("}") || lineTrim.endsWith("]")
    val hasComplexValueStartNextLine = !lineTrim.endsWith(",") && (textEnding.startsWith("{") || textEnding.startsWith(
      "["))
    val hasComplexValueNextLine = !lineTrim.endsWith(",") & (hasComplexValueStartNextLine || textEnding.startsWith("}") || textEnding
      .startsWith("]"))
    val hasComplexValueStart = hasComplexValueStartNextLine || hasComplexValueStartSameLine
    var needComa             = !(lineTrim.endsWith(",") || hasComplexValueNextLine || hasComplexValueSameLine)
    if (needComa) {
      val textEnding = text.substring(lineEnd).trim
      needComa = textEnding.nonEmpty && !(textEnding.startsWith(",") || textEnding.startsWith("{") || textEnding
        .startsWith("}") || textEnding.startsWith("[") || textEnding.startsWith("]"))
    }
    var colonIndex = line.indexOf(":")
    var newLine    = line
    if (colonIndex < 0) {
      if (lineTrim.startsWith("\"")) {
        newLine = line.substring(0, off) + "x\" : "
        if (!hasComplexValueStart)
          newLine += "\"\""
        if (!(hasComplexValueSameLine || hasComplexValueNextLine))
          newLine += ","
      }
    } else if (colonIndex <= off) {
      colonIndex = line.lastIndexOf(":", off)
      var substr               = line.substring(colonIndex + 1).trim
      val hasOpenCurlyBracket  = substr.startsWith("{")
      val hasOpenSquareBracket = substr.startsWith("[")
      newLine = line.substring(0, off)
      if (hasOpenCurlyBracket || hasOpenSquareBracket)
        substr = substr.substring(1)
      var hasOpenValueQuote = substr.startsWith("\"")
      if (!hasOpenValueQuote && !(hasOpenCurlyBracket || hasOpenSquareBracket)) {
        newLine += "\""
        hasOpenValueQuote = true
      }
      if (hasOpenValueQuote)
        newLine += "\""
      if (hasComplexValueSameLine)
        newLine += lineTrim.charAt(lineTrim.length - 1)
      if (lineTrim.endsWith(","))
        newLine += ","
    } else {
      if (line.substring(colonIndex + 1).trim.startsWith("\"")) {
        val openQuoteInd = line.indexOf("\"", colonIndex)
        if (off > openQuoteInd)
          if (!lineTrim.endsWith("\""))
            newLine += "\""
      }
      if (needComa)
        newLine += ","
    }
    val result = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
    result.replace("\n", EOL)
  }
}
