package org.mulesoft.als.suggestions.patcher

import org.mulesoft.als.suggestions.implementation.LocationKindDetectTool
import org.mulesoft.als.suggestions.interfaces.LocationKind.{
  ANNOTATION_COMPLETION,
  KEY_COMPLETION,
  SEQUENCE_KEY_COPLETION
}

object YamlContentPatcher {

  def prepareYamlContent(text: String, offset: Int): PatchedContent = {
    val completionKind =
      LocationKindDetectTool.determineCompletionKind(text, offset)
    val result = completionKind match {
      case KEY_COMPLETION | ANNOTATION_COMPLETION | SEQUENCE_KEY_COPLETION =>
        val newLineIndex = text.indexOf("\n", offset)
        val rightPart =
          if (newLineIndex < 0) text.substring(offset)
          else text.substring(offset, newLineIndex)
        val colonIndex = rightPart.indexOf(":")
        val leftPart   = text.substring(0, offset)
        val leftOfSentence =
          leftPart.substring(0 max leftPart.lastIndexOf('\n'), offset)
        if (colonIndex < 0)
          text.substring(0, offset) + "k: " + text.substring(offset)
        else if (colonIndex == 0) {
          val rightPart = text.substring(offset)
          val rightOfSentence =
            rightPart.substring(0, rightPart.length min (0 max rightPart.indexOf('\n')))

          val openBrackets = {
            leftOfSentence + rightOfSentence
          }.count(_ == '[') - {
            leftOfSentence + rightOfSentence
          }.count(_ == '[')
          text + "k" + " ]" * openBrackets + rightPart
        } else text
      case _ =>
        if (offset == text.length) text + " " + "\n"
        else text
    }
    PatchedContent(result, text, Nil) // add same logic that for json?
  }
}
