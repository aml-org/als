package org.mulesoft.als.suggestions.patcher

import scala.collection.mutable.ListBuffer


class JsonContentPatcher(override val textRaw:String, override val offsetRaw:Int) extends ContentPatcher {
  private val tokens:ListBuffer[PatchToken] = ListBuffer()

  def listTokens(): List[PatchToken] = tokens.toList

  override def prepareContent(): PatchedContent = {
    val content = patch()
    PatchedContent(content,textRaw,listTokens())
  }

  def patch(): String = {
    val EOL       = textRaw.find(_ == '\r').map(_ => "\r\n").getOrElse("\n")
    val text      = textRaw.replace(EOL, "\n")
    val offset    = offsetRaw - textRaw.substring(0, offsetRaw).count(_ == '\r')
    val lineStart = 0.max(text.lastIndexOf("\n", 0.max(offset - 1)) + 1)

    var lineEnd = text.indexOf("\n", offset)
    if (lineEnd < 0) lineEnd = text.length
    val line       = text.substring(lineStart, lineEnd)
    val off        = offset - lineStart
    val lineTrim   = line.trim
    val textEnding = text.substring(lineEnd + 1).trim
    val hasComplexValueStartSameLine = lineTrim.endsWith("{") || lineTrim
      .endsWith("[")
    val hasComplexValueSameLine = hasComplexValueStartSameLine || lineTrim
      .endsWith("}") || lineTrim.endsWith("]")
    val hasComplexValueStartNextLine = !lineTrim.endsWith(",") && (textEnding
      .startsWith("{") || textEnding.startsWith("["))
    val hasComplexValueNextLine = !lineTrim.endsWith(",") & (hasComplexValueStartNextLine || textEnding
      .startsWith("}") || textEnding
      .startsWith("]"))
    val hasComplexValueStart = hasComplexValueStartNextLine || hasComplexValueStartSameLine
    var needComa =
      !(lineTrim.endsWith(",") || hasComplexValueNextLine || hasComplexValueSameLine)
    if (needComa) {
      val textEnding = text.substring(lineEnd).trim
      needComa = textEnding.nonEmpty && !(textEnding.startsWith(",") || textEnding
        .startsWith("{") || textEnding
        .startsWith("}") || textEnding.startsWith("[") || textEnding.startsWith("]"))
    }
    var colonIndex = line.indexOf(":")
    var newLine    = line
    if (colonIndex < 0) {
      if (lineTrim.startsWith("\"")) {
        if (lineTrim.endsWith("\"") && lineTrim.length > 2) newLine = addColon(line.substring(0, off)+"\"")
        else newLine = addColon(addQuote(line.substring(0, off) + "x"))
        if (!hasComplexValueStart)
          newLine = addQuote(addQuote(newLine))
        if (!(hasComplexValueSameLine || hasComplexValueNextLine))
          newLine = addComma(newLine)
      } else newLine = newLine + "\n"
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
        newLine = addQuote(newLine)
        hasOpenValueQuote = true
      }
      if (hasOpenValueQuote)
        newLine = addQuote(newLine)
      if (hasComplexValueSameLine)
        newLine += lineTrim.charAt(lineTrim.length - 1)
      if (lineTrim.endsWith(","))
        newLine += ","
    } else {
      if (line.substring(colonIndex + 1).trim.startsWith("\"")) {
        val openQuoteInd = line.indexOf("\"", colonIndex)
        if (off > openQuoteInd)
          if (!lineTrim.endsWith("\""))
            newLine = addQuote(newLine)
      }
      newLine.split(':').toList match {
        case head :: tail if tail.nonEmpty =>
          val entryValue = tail.last
          val prefix: String = entryValue.trim.headOption.getOrElse("") match {
            case '-' | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' => ""
            case 't' | 'f'                                                       => ""
            case '{' | '[' | '"'                                                 => ""
            case ","                                                             => addQuote("")
            case _                                                               => addQuote("")
          }
          val newEntryValue =
            if (entryValue == ",") ""
            else if (entryValue.endsWith(",") && entryValue.length > 1)
              entryValue.substring(0, entryValue.length - 2)
            else entryValue
          val postFix =
            if (prefix.nonEmpty && !entryValue.trim.endsWith("\"")) addQuote("") else ""

          val postFixFinal = if(lineTrim.endsWith(",")) postFix + "," else addComma(postFix)
          needComa = false
          newLine = head + ":" + prefix + newEntryValue + postFixFinal
        case head :: Nil if needComa =>
          newLine = addComma(addQuote(addQuote(newLine)))
          needComa = false
      }
      if (needComa)
        newLine = addComma(newLine)
    }
    val result = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
    result.replace("\n", EOL)
  }


  def addColon(line:String):String = {
    tokens += ColonToken
    line + " : "
  }

  def addQuote(line:String):String= {
    tokens += QuoteToken
    line + "\""
  }

  def addComma(line:String):String = {
    tokens += CommaToken
    line + ","
  }
}