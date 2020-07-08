package org.mulesoft.als.actions.common

import org.mulesoft.als.actions.common.LinkTypes.LinkTypes
import org.mulesoft.als.actions.common.YPartImplicits._
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YMapEntry, YPart}

case class RelationshipLink(sourceEntry: YPart,
                            targetEntry: YPart,
                            nameRange: Option[YPart] = None,
                            linkType: LinkTypes = LinkTypes.OTHER) {
  def destination: Location =
    if (targetEntry.sourceName.isEmpty)
      // the map is empty, this is probably an error with the annotations but I can save it if there is a name
      nameRange.map(_.yPartToLocation).getOrElse(targetEntry.yPartToLocation)
    else targetEntry.yPartToLocation

  def source: Location = sourceEntry match {
    case e: YMapEntry => e.value.yPartToLocation
    case _            => sourceEntry.yPartToLocation
  }
}

object LinkTypes extends Enumeration {
  type LinkTypes = Value
  val OTHER, TRAITRESOURCES = Value
}
