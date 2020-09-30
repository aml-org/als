package org.mulesoft.amfintegration.relationships

import org.mulesoft.amfintegration.relationships.LinkTypes.LinkTypes
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YMapEntry, YPart}

case class RelationshipLink(sourceEntry: YPart,
                            targetEntry: YPart,
                            private val namePart: Option[YPart] = None,
                            linkType: LinkTypes = LinkTypes.OTHER) {
  def destination: Location =
    if (targetEntry.sourceName.isEmpty)
      // the map is empty, this is probably an error with the annotations but I can save it if there is a name
      this.namePart.map(_.yPartToLocation).getOrElse(targetEntry.yPartToLocation)
    else targetEntry.yPartToLocation

  def source: Location = sourceEntry match {
    case e: YMapEntry => e.value.yPartToLocation
    case _            => sourceEntry.yPartToLocation
  }
  def nameYPart: YPart = namePart.getOrElse(targetEntry)

  def relationshipIsEqual(other: RelationshipLink): Boolean =
    other.source == this.source && other.targetEntry == this.targetEntry
}

object LinkTypes extends Enumeration {
  type LinkTypes = Value
  val OTHER, TRAITRESOURCES = Value
}
