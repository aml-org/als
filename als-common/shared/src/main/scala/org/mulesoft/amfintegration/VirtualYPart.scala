package org.mulesoft.amfintegration

import amf.core.annotations.LexicalInformation
import org.mulesoft.als.common.YamlWrapper.AlsInputRange
import org.mulesoft.lexer.{InputRange, SourceLocation}
import org.yaml.model.{YScalar, YValue}

/**
  * Virtual YParts are not really part of the AST, but group similar information
  *   for the node (for example, a specific scalar parsed inside another scalar by AMF)
  */
case class VirtualYPart(override val location: SourceLocation, text: String)
    extends YValue(location, IndexedSeq.empty) {
  override def toString: String = text
}

object VirtualYPart {
  def apply(originalPart: YScalar, name: String, maybeLexical: Option[LexicalInformation]): VirtualYPart = {
    val originalFullText = originalPart.text
    maybeLexical match {
      case Some(li)
          if !originalPart.range.isEqual(li) => // Lexical Information is a subset of the actual AST, already divided
        val offset      = Math.max(li.range.start.column - originalPart.range.columnFrom, 0)
        val subLeft     = originalFullText.substring(offset)
        val offsetRight = subLeft.indexOf(name) + name.length
        val partText    = subLeft.substring(0, offsetRight)
        VirtualYPart(buildLocation(originalPart.location.sourceName, li), partText)
      case _ =>
        val length = originalFullText.indexOf(name) + name.length
        val lexicalInformation = LexicalInformation(originalPart.range.lineFrom,
                                                    originalPart.range.columnFrom,
                                                    originalPart.range.lineTo,
                                                    originalPart.range.columnFrom + length)
        VirtualYPart(buildLocation(originalPart.location.sourceName, lexicalInformation),
                     originalFullText.substring(0, length))
    }
  }

  private def buildLocation(location: String, information: LexicalInformation) =
    SourceLocation(location,
                   InputRange(information.range.start.line,
                              information.range.start.column,
                              information.range.end.line,
                              information.range.end.column))

}
