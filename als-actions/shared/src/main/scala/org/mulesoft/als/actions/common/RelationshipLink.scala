package org.mulesoft.als.actions.common

import org.mulesoft.als.actions.common.LinkTypes.LinkTypes
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YMapEntry, YPart}
import org.mulesoft.als.actions.common.YPartImplicits._

case class RelationshipLink(sourceEntry: YPart,
                            targetEntry: YPart,
                            nameRange: Option[PositionRange] = None,
                            linkType: LinkTypes = LinkTypes.OTHER) {
  def destination: Location = targetEntry.yPartToLocation
  def parentEntry: Option[Location] = sourceEntry match {
    case e: YMapEntry => Some(e.yPartToLocation)
    case _            => None
  }
  def source: Location = sourceEntry match {
    case e: YMapEntry => e.value.yPartToLocation
    case _            => sourceEntry.yPartToLocation
  }
}

object LinkTypes extends Enumeration {
  type LinkTypes = Value
  val OTHER, TRAITRESOURCES = Value
}
