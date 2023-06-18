package org.mulesoft.lsp.feature.common

import org.mulesoft.exceptions.PathTweaks

case class LocationLink(
    targetUri: String,
    targetRange: Range,
    targetSelectionRange: Range,
    originSelectionRange: Option[Range] = None
)

object LocationLink {
  def apply(
      targetUri: String,
      targetRange: Range,
      targetSelectionRange: Range,
      originSelectionRange: Option[Range] = None
  ) = new LocationLink(PathTweaks.tweak(targetUri), targetRange, targetSelectionRange, originSelectionRange)
  def apply(t: (Location, Location)): LocationLink =
    new LocationLink(t._2.uri, t._2.range, t._2.range, Some(t._1.range))
}
