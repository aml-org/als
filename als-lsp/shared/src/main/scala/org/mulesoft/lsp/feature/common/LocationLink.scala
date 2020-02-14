package org.mulesoft.lsp.feature.common

case class LocationLink(targetUri: String, targetRange: Range, targetSelectionRange: Range, originSelectionRange: Option[Range] = None)
