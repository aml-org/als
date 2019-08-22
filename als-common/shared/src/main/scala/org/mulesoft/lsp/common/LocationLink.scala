package org.mulesoft.lsp.common

case class LocationLink(targetUri: String, targetRange: Range, targetSelectionRange: Range, originSelectionRange: Option[Range] = None)
