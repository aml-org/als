package org.mulesoft.amfintegration.relationships

import org.mulesoft.als.common.ASTElementWrapper._
import org.mulesoft.amfintegration.relationships.LinkTypes.LinkTypes
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YMapEntry, YPart}

case class RelationshipLink(
    sourceEntry: YPart,
    targetEntry: YPart,
    private val targetName: Option[YPart] = None,
    private val sourceName: Option[YPart] = None,
    linkType: LinkTypes = LinkTypes.OTHER
) {
  def destination: Location =
    if (targetEntry.sourceName.isEmpty)
      // the map is empty, this is probably an error with the annotations but I can save it if there is a name
      this.targetName.map(_.astToLocation).getOrElse(targetEntry.astToLocation)
    else targetEntry.astToLocation

  def source: Location =
    sourceName
      .map(_.astToLocation)
      .getOrElse(sourceEntry match {
        case e: YMapEntry => e.value.astToLocation
        case _            => sourceEntry.astToLocation
      })

  def sourceNameEntry: YPart = sourceName.getOrElse(sourceEntry)

  def targetNamePart: YPart = targetName.getOrElse(targetEntry)

  def relationshipIsEqual(other: RelationshipLink): Boolean =
    other.source == this.source && other.targetEntry == this.targetEntry
}

object LinkTypes extends Enumeration {
  type LinkTypes = Value
  val OTHER, TRAITRESOURCES = Value
}
