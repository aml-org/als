package org.mulesoft.als.suggestions

import org.mulesoft.als.common.YPartASTWrapper.YNodeImplicits
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.declarations.DeclarationCreator
import org.yaml.model.{YMapEntry, YNode, YPart}

case class AdditionalSuggestion(insert: YPart, range: Either[PositionRange, YMapEntry])

object AdditionalSuggestion extends DeclarationCreator {

  /** @param insert
    *   full node which should be added
    * @param into
    *   navigates through existing keys
    * @return
    */
  def apply(insert: YNode, into: Seq[String], ast: YPart, defaultRange: PositionRange): AdditionalSuggestion =
    findPositionRange(insert, into.reverse, ast) match {
      case (part, Some(parent)) => new AdditionalSuggestion(part, Right(parent))
      case (part, None)         => new AdditionalSuggestion(part, Left(defaultRange))
    }

  private def findPositionRange(insert: YNode, into: Seq[String], ast: YPart): (YPart, Option[YMapEntry]) = {
    var fullPath                = insert
    val entries: Seq[YMapEntry] = getExistingParts(Some(ast), into)
    into
      .dropRight(entries.size)
      .foreach(k => fullPath = fullPath.withKey(k))
    (fullPath, entries.lastOption)
  }

}
