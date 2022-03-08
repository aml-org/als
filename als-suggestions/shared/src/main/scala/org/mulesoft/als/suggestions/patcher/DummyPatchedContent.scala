package org.mulesoft.als.suggestions.patcher

import org.mulesoft.als.suggestions.implementation.LocationKindDetectTool
import org.mulesoft.als.suggestions.interfaces.LocationKind.{
  ANNOTATION_COMPLETION,
  KEY_COMPLETION,
  SEQUENCE_KEY_COPLETION
}

import scala.collection.mutable

/* TODO:
    when removing patcher, erase LocationKindDetectTool, LocationKind, Point, YPoint, PositionsMapper and IPositionsMapper
 */
class DummyPatchedContent(override val textRaw: String, override val offsetRaw: Int) extends ContentPatcher {
  val content                                   = PatchedContent(textRaw, textRaw, Nil) // add same logic that for json?
  override def prepareContent(): PatchedContent = content
}
