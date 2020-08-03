package org.mulesoft.amfintegration

import amf.core.annotations.LexicalInformation
import org.mulesoft.lexer.{InputRange, SourceLocation}
import org.yaml.model.YValue

/**
  * Virtual YParts are not really part of the AST, but group similar information
  *   for the node (for example, a specific scalar parsed inside another scalar by AMF)
  */
case class VirtualYPart(override val location: SourceLocation, text: String)
    extends YValue(location, IndexedSeq.empty) {
  override def toString: String = text
}

object VirtualYPart {
  def apply(location: String, range: LexicalInformation, text: String): VirtualYPart =
    VirtualYPart(buildLocation(location, range), text)

  private def buildLocation(location: String, information: LexicalInformation) =
    SourceLocation(location,
                   InputRange(information.range.start.line,
                              information.range.start.column,
                              information.range.end.line,
                              information.range.end.column))

}
