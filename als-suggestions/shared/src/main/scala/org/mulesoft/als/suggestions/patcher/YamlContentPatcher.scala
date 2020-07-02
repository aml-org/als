package org.mulesoft.als.suggestions.patcher

import org.mulesoft.als.suggestions.implementation.LocationKindDetectTool
import org.mulesoft.als.suggestions.interfaces.LocationKind.{
  ANNOTATION_COMPLETION,
  KEY_COMPLETION,
  SEQUENCE_KEY_COPLETION
}

import scala.collection.mutable

class YamlContentPatcher(override val textRaw: String, override val offsetRaw: Int) extends ContentPatcher {

  override def prepareContent(): PatchedContent = {
    val tokens: mutable.ListBuffer[PatchToken] = mutable.ListBuffer()
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
        if (colonIndex < 0) {
          val tuple = insertKWithColon(rightPart)
          tuple._2.foreach(t => tokens += t)
          tuple._1
        } else if (colonIndex == 0) {
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

    PatchedContent(result, textRaw, tokens.toList) // add same logic that for json?
  }

  private def insertKWithColon(rightPart: String): (String, List[PatchToken]) = {
    val pre  = textRaw.substring(0, offsetRaw)
    val post = textRaw.substring(offsetRaw)
    if (rightPart.contains("'"))
      (pre + "k': " + post.replaceFirst("'", ""), List(ColonToken, QuoteToken))
    else if (rightPart.contains("\""))
      (pre + "k\": " + post.replaceFirst("\"", ""), List(ColonToken, QuoteToken))
    else
      (pre + "k: " + post, List(ColonToken))
  }
}
