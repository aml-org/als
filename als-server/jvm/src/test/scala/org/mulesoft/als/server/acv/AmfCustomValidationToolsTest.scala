package org.mulesoft.als.server.acv

import amf.core.internal.unsafe.PlatformSecrets
import org.eclipse.lsp4j.{Position, Range}
import org.scalatest.funsuite.AsyncFunSuite

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.jdk.CollectionConverters.collectionAsScalaIterableConverter

class AmfCustomValidationToolsTest extends AsyncFunSuite with PlatformSecrets {
  private def buildResourcePath(file: String) = s"als-server/jvm/src/test/resources/acv/$file"

  test("parse report into java class") {
    for {
      profileContent <- platform.fs.asyncFile(buildResourcePath("profile.yaml")).read()
      reportContent  <- platform.fs.asyncFile(buildResourcePath("report.jsonld")).read()
      parsed <- AmfCustomValidationTools
        .parseReport(reportContent.toString, "file:///api.raml", profileContent.toString)
        .toScala
    } yield {
      assert(parsed.getEntries.size() == 1)
      assert(parsed.getEntries.get(0).getLevel == "Violation")
      assert(parsed.getEntries.get(0).getName == "validation1")
    }
  }

  test("parse nested-traces") {
    for {
      profileContent <- platform.fs.asyncFile(buildResourcePath("nested-traces.yaml")).read()
      reportContent  <- platform.fs.asyncFile(buildResourcePath("nested-traces.jsonld")).read()
      parsed <- AmfCustomValidationTools
        .parseReport(reportContent.toString, "file:///acv-test.raml", profileContent.toString)
        .toScala
    } yield {
      assert(parsed.getProfileName == "Test13")
      assert(parsed.getEntries.size() == 1)
      val entry =
        parsed.getEntries.asScala.find(c => c.getName == "lack-of-resources-and-rate-limiting-too-many-requests").get
      assert(entry.getLocation.getUri == "file://./test/data/integration/profile13/negative.data.yaml")
      assert(entry.getLocation.getRange == new Range(new Position(19, 4), new Position(25, 28)))
      val traces = entry.getTrace.asScala
      assert(traces.size == 2)
      val trace200 = traces.find(
        _.getTraces.asScala.head.getMessage.contains("Error expected [\"200\"] but got actual (actual=429)")
      )
      val trace403 = traces.find(
        _.getTraces.asScala.head.getMessage.contains("Error expected [\"403\"] but got actual (actual=429)")
      )
      assert(
        trace200.exists(
          _.getTraces.asScala.exists(_.getLocation.getRange == new Range(new Position(21, 8), new Position(25, 28)))
        )
      )
      assert(
        trace403.exists(
          _.getTraces.asScala.exists(_.getLocation.getRange == new Range(new Position(21, 8), new Position(25, 28)))
        )
      )
    }
  }
}
