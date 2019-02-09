package org.mulesoft.language.common.dtoTypes

/**
  * Validation report.
  *
  * @param pointOfViewUri This is the "point of view" uri, actual reported unit paths are located
  *                       in the particular issues.
  * @param version        Optional document version of the point of view.
  * @param issues         Validation issues.
  *
  */
case class ValidationReport(pointOfViewUri: String, version: Int, issues: Seq[ValidationIssue])
