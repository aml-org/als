package org.mulesoft.lsp.feature.common

case class LocationLink(
    targetUri: String,
    targetRange: Range,
    targetSelectionRange: Range,
    originSelectionRange: Option[Range] = None
)

object LocationLink {
  def apply(t: (Location, Location)): LocationLink =
    new LocationLink(t._2.uri, t._2.range, t._2.range, Some(t._1.range))
}
