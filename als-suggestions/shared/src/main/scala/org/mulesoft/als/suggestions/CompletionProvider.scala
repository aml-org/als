package org.mulesoft.als.suggestions

import common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.implementation.{CompletionRequest, LocationKindDetectTool, Suggestion}
import org.mulesoft.als.suggestions.interfaces.LocationKind._
import org.mulesoft.als.suggestions.interfaces.{Suggestion => SuggestionInterface, _}
import org.mulesoft.high.level.interfaces.{IASTUnit, IParseResult}
import org.mulesoft.positioning.{PositionsMapper, YamlLocation, YamlSearch}
import org.yaml.model.YPart

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProvider {
  var _config: ICompletionConfig = _

  var _pluginsRegistry: ICompletionPluginsRegistry = CompletionPluginsRegistry.instance

  def withConfig(cfg: ICompletionConfig): CompletionProvider = {
    _config = cfg
    this
  }

  def suggest: Future[Seq[SuggestionInterface]] = suggest(true)

  def suggest(filterByPrefix: Boolean): Future[Seq[SuggestionInterface]] = {
    val request = composeRequest
    val range = Option(
      PositionRange(Position(request.position - request.prefix.size, _config.originalContent.get),
                    Position(request.position, _config.originalContent.get)))
    fulfillRequest(request, range).map(result => {
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
        prefix = CompletionProvider.getPrefix(esp)

        position = esp.getOffset

        currentIndent = CompletionProvider.getCurrentIndent(esp)

        indentCount = CompletionProvider.getCurrentIndentCount(esp)
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
        val ast                                      = CompletionProvider.getAstNode(position, prefix, ap)
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

  def fulfillRequest(request: ICompletionRequest, range: Option[PositionRange]): Future[Seq[SuggestionInterface]] = {

    val filteredPlugins = _pluginsRegistry.plugins.filter(plugin => {
      plugin.isApplicable(request)
    })
    Future
      .sequence(
        filteredPlugins.map(_.suggest(request))
      )
      .map(responses => responses.flatMap(r => adjustedSuggestions(r, range)))
  }

  def adjustedSuggestions(response: ICompletionResponse, range: Option[PositionRange]): Seq[SuggestionInterface] = {
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
        if (colonIndex > 0) {
          hasKeyClosingQuote = tail.substring(0, colonIndex).trim.endsWith("\"")
        } else {
          hasKeyClosingQuote = hasQuote
        }
      }
    }

    var result = response.suggestions
    if (isYAML) {
      if (!response.noColon && isKey) {
        if (!hasLine || !hasColon) {
          result = result.map(x => {
            val newText = x.text + ":" + { if (x.trailingWhitespace.isEmpty) " " else x.trailingWhitespace }
            Suggestion(newText, x.description, x.displayText, x.prefix, range).withCategory(x.category)
          })
        }
      } else if (!isKey) {
        result = result.map(x => {
          val prefix = x.prefix
          if (prefix == ":" && (!x.text.startsWith("\n") || x.text.startsWith("\r\n") || x.text.startsWith(" "))) {
            Suggestion(" " + x.text, x.description, x.displayText, x.prefix, range).withCategory(x.category)
          } else {
            Suggestion({ if (x.text.endsWith(":")) s"${x.text} " else x.text },
                       x.description,
                       x.displayText,
                       x.prefix,
                       range).withCategory(x.category)
          }
        })
      }
    } else if (isJSON) {
      var postfix     = ""
      var endingQuote = false
      if (isKey) {
        if (!hasKeyClosingQuote) {
          postfix += "\""
          if (!hasColon && !response.noColon) {
            postfix += ":"
          }
        } else if (!hasQuote) {
          postfix += "\""
        }
      } else if (!hasQuote) {
        postfix += "\""
        endingQuote = true
      }
      if (postfix.nonEmpty) {
        result = result.map(x => {
          val isJSONObject = isJSON && x.text.startsWith("{") && x.text.endsWith("}")
          val newText      = if (!isJSONObject && (!endingQuote || !x.text.endsWith("\""))) x.text + postfix else x.text
          Suggestion(newText, x.description, x.displayText, x.prefix, range).withCategory(x.category)
        })
      }
    }
    result
  }

  def filter(suggestions: Seq[SuggestionInterface], request: ICompletionRequest): Seq[SuggestionInterface] = {
    suggestions.filter(s => {
      val prefix = s.prefix.toLowerCase
      if (prefix.isEmpty || prefix == ":" || prefix == "/") true
      else s.displayText.toLowerCase.startsWith(prefix)
    })
  }
}

object CompletionProvider {
  private val prefixRegex = """(\b|['"~`!@#\$%^&*\(\)\{\}\[\]=\+,\/\?>])?(([\w\.]+[\w-\/\.]*)|()|([.:;\[{\(< ]+))$""".r

  def apply(): CompletionProvider = new CompletionProvider()

  def getPrefix(content: IEditorStateProvider): String = {
    val textTrim = content.getText.trim
    val isJSON   = textTrim.startsWith("{") && textTrim.endsWith("}")
    val line     = getLine(content)
    val opt      = prefixRegex.findFirstIn(line)
    var result   = opt.getOrElse("")
    if (result.startsWith("\"")) {
      result = result.substring(1)
    }
    val text   = content.getText
    val offset = content.getOffset
    if (offset > 0 && text.lastIndexOf("\n", offset - 1) < 0 && text.substring(0, offset) == "#%RAML 1.0") {
      result = ""
    }
    if (isJSON && result == "{") {
      result = ""
    }
    result
  }

  def getLine(content: IEditorStateProvider): String = {
    val offset: Int  = content.getOffset
    val text: String = content.getText

    var result = ""
    val ind    = text.lastIndexWhere(c => { c == '\r' || c == '\n' || c == ' ' || c == '\t' }, offset - 1)

    if (ind >= 0) {
      result = text.substring(ind + 1, offset)
    }

    result
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
    Math.max(0, getIndentation(content.getText, content.getOffset).length / getCurrentIndent(content).length)
  }

  def valuePrefix(content: IEditorStateProvider): String = {
    val offset = content.getOffset
    val text   = content.getText
    var result = ""
    val ind = text.lastIndexWhere(c => {
      c == '\r' || c == '\n' || c == ' ' || c == '\t' || c == '\"' || c == ''' || c == ':' || c == '('
    }, offset - 1)
    if (ind >= 0) {
      result = text.substring(ind + 1, offset)
    }
    result
  }
  def getAstNode(position: Int, prefix: String, astProvider: IASTProvider): (Option[IParseResult], Option[YPart]) = {
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
    result // + "\n"
  }

  def prepareJsonContent(text: String, offset: Int): String = {
    var lineStart = text.lastIndexOf("\n", Math.max(0, offset - 1)) match {
      case lStart if lStart < 0 => 0
      case lStart               => lStart + 1
    }
    if (lineStart < 0) lineStart = 0
    else lineStart += 1

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
        if (!hasComplexValueStart) {
          newLine += "\"\""
        }
        if (!(hasComplexValueSameLine || hasComplexValueNextLine)) {
          newLine += ","
        }
      }
    } else if (colonIndex <= off) {
      colonIndex = line.lastIndexOf(":", off)
      var substr               = line.substring(colonIndex + 1).trim
      val hasOpenCurlyBracket  = substr.startsWith("{")
      val hasOpenSquareBracket = substr.startsWith("[")
      newLine = line.substring(0, off)
      if (hasOpenCurlyBracket || hasOpenSquareBracket) {
        substr = substr.substring(1)
      }
      var hasOpenValueQuote = substr.startsWith("\"")
      if (!hasOpenValueQuote && !(hasOpenCurlyBracket || hasOpenSquareBracket)) {
        newLine += "\""
        hasOpenValueQuote = true
      }
      if (hasOpenValueQuote) {
        newLine += "\""
      }
      if (hasComplexValueSameLine) {
        newLine += lineTrim.charAt(lineTrim.length - 1)
      }
      if (lineTrim.endsWith(",")) {
        newLine += ","
      }
    } else {
      if (line.substring(colonIndex + 1).trim.startsWith("\"")) {
        val openQuoteInd = line.indexOf("\"", colonIndex)
        if (off > openQuoteInd) {
          if (!lineTrim.endsWith("\"")) {
            newLine += "\""
          }
        }
      }
      if (needComa) {
        newLine += ","
      }
    }
    val result = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
    result
  }
}
