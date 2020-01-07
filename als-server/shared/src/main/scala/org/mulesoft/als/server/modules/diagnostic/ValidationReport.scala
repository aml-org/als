package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

/**
  * Validation report.
  *
  * @param pointOfViewUri This is the "point of view" uri, actual reported unit paths are located
  *                       in the particular issues.
  * @param issues         Validation issues.
  *
  */
case class ValidationReport(pointOfViewUri: String, issues: Set[ValidationIssue]) {
  lazy val publishDiagnosticsParams: PublishDiagnosticsParams = AlsPublishDiagnosticsParams(
    pointOfViewUri,
    issues.map(_.diagnostic).toSeq
  )
}
