package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.{ProfileNames, SeverityLevels}
import amf.core.client.scala.validation.AMFValidationResult
import org.scalatest.{FunSuite, Matchers}

class DiagnosticConverterTest extends FunSuite with Matchers {

  private val withoutLocation =
    AMFValidationResult("A message without location", SeverityLevels.VIOLATION, "", None, "", None, None, null)
  private val located = AMFValidationResult("LocatedMessage",
                                            SeverityLevels.VIOLATION,
                                            "",
                                            None,
                                            "",
                                            None,
                                            Some("file://reference.raml"),
                                            null)

  test("Test diagnostic without location") {

    val reports = DiagnosticConverters.buildIssueResults(Map("file://root.raml" -> Seq(withoutLocation)),
                                                         Map.empty,
                                                         ProfileNames.RAML10)
    reports.size should be(1)
    val report = reports.head
    report.pointOfViewUri should be("file://root.raml")
    report.issues.size should be(1)
    val issue = report.issues.head
    issue.filePath should be("file://root.raml")
    issue.text should be("A message without location")
    issue.trace.size should be(1)
  }

  test("Test diagnostic with and without location") {
    val reports = DiagnosticConverters.buildIssueResults(
      Map("file://root.raml" -> Seq(withoutLocation), "file://reference.raml" -> Seq(located)),
      Map.empty,
      ProfileNames.RAML10)
    reports.size should be(2)
    val report = reports.head
    report.pointOfViewUri should be("file://reference.raml")
    report.issues.size should be(1)
    val issue = report.issues.head
    issue.filePath should be("file://reference.raml")
    issue.text should be("LocatedMessage")
    issue.trace.size should be(0)

    val last = reports.last
    last.pointOfViewUri should be("file://root.raml")
    last.issues.size should be(1)
    val lastIssue = last.issues.head
    lastIssue.filePath should be("file://root.raml")
    lastIssue.text should be("A message without location")
    lastIssue.trace.size should be(1)
  }
}
