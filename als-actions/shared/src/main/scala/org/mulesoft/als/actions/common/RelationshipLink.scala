package org.mulesoft.als.actions.common

import org.mulesoft.als.actions.common.LinkTypes.LinkTypes
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YMapEntry, YPart}

case class RelationshipLink(sourceEntry: YPart, targetEntry: YPart, linkType: LinkTypes = LinkTypes.OTHER) {
  def destination: Location = ActionTools.yPartToLocation(targetEntry)
  def parentEntry: Option[Location] = sourceEntry match {
    case e: YMapEntry => Some(ActionTools.yPartToLocation(e))
    case _            => None
  }
  def source: Location = sourceEntry match {
    case e: YMapEntry => ActionTools.yPartToLocation(e.value)
    case _            => ActionTools.yPartToLocation(sourceEntry)
  }
}

object LinkTypes extends Enumeration {
  type LinkTypes = Value
  val OTHER, TRAITRESOURCES = Value
}
