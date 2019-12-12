package org.mulesoft.als.suggestions.patcher

import org.mulesoft.als.suggestions.implementation.LocationKindDetectTool
import org.mulesoft.als.suggestions.interfaces.LocationKind.{
  ANNOTATION_COMPLETION,
  KEY_COMPLETION,
  SEQUENCE_KEY_COPLETION
}

class YamlContentPatcher(override val textRaw: String, override val offsetRaw: Int) extends ContentPatcher {

  override def prepareContent(): PatchedContent = {
    val completionKind =
      LocationKindDetectTool.determineCompletionKind(textRaw, offsetRaw)
    val result = completionKind match {
      case KEY_COMPLETION | ANNOTATION_COMPLETION | SEQUENCE_KEY_COPLETION =>
        val newLineIndex = textRaw.indexOf("\n", offsetRaw)
        val rightPart =
          if (newLineIndex < 0) textRaw.substring(offsetRaw)
          else textRaw.substring(offsetRaw, newLineIndex)
        val colonIndex = rightPart.indexOf(":")
        val leftPart   = textRaw.substring(0, offsetRaw)
        val leftOfSentence =
          leftPart.substring(0 max leftPart.lastIndexOf('\n'), offsetRaw)
        if (colonIndex < 0)
          textRaw.substring(0, offsetRaw) + "k: " + textRaw.substring(offsetRaw)
        else if (colonIndex == 0) {
          val rightPart = textRaw.substring(offsetRaw)
          val rightOfSentence =
            rightPart.substring(0, rightPart.length min (0 max rightPart.indexOf('\n')))

          val openBrackets = { leftOfSentence + rightOfSentence }
            .count(_ == '[') - {
            leftOfSentence + rightOfSentence
          }.count(_ == '[')
          textRaw + "k" + " ]" * openBrackets + rightPart
        } else textRaw
      case _ =>
        if (offsetRaw == textRaw.length) textRaw + " " + "\n"
        else textRaw
    }

    PatchedContent(result, textRaw, Nil) // add same logic that for json?
  }
}
