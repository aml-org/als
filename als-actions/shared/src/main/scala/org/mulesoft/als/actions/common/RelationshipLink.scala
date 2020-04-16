package org.mulesoft.als.actions.common

import org.mulesoft.als.actions.common.LinkTypes.LinkTypes
import org.mulesoft.lsp.feature.common.Location

case class RelationshipLink(source: Location,
                            destination: Location,
                            parentEntry: Option[Location],
                            linkType: LinkTypes = LinkTypes.OTHER)

object LinkTypes extends Enumeration {
  type LinkTypes = Value
  val OTHER, TRAITRESOURCES = Value
}
