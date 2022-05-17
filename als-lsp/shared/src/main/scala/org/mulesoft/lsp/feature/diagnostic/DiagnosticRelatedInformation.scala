package org.mulesoft.lsp.feature.diagnostic

import org.mulesoft.lsp.feature.common.Location

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Represents a related message and source code location for a diagnostic. This should be used to point to code
  * locations that cause or related to a diagnostics, e.g when duplicating a symbol in a scope.
  *
  * @param location
  *   The location of this related diagnostic information.
  * @param message
  *   The message of this related diagnostic information.
  */
@JSExportAll
@JSExportTopLevel("DiagnosticRelatedInformation")
case class DiagnosticRelatedInformation(location: Location, message: String)
