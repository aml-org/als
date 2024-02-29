package org.mulesoft.als.server.acv

import amf.core.internal.unsafe.PlatformSecrets
import org.scalatest.funsuite.AsyncFunSuite

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.jdk.CollectionConverters.collectionAsScalaIterableConverter

import org.eclipse.lsp4j.{Range, Location, Position}

class AmfCustomValidationToolsTest extends AsyncFunSuite with PlatformSecrets {
//file://als-server/shared/src/test/resources/diagnostics/project/api.raml

  private def buildResourcePath(file: String) = s"als-server/jvm/src/test/resources/acv/$file"

  test("parse report into java class") {
    for {
      profileContent <- platform.fs.asyncFile(buildResourcePath("profile.yaml")).read()
      reportContent  <- platform.fs.asyncFile(buildResourcePath("report.jsonld")).read()
      parsed <- AmfCustomValidationTools
        .parseReport(reportContent.toString, "file:///api.raml", profileContent.toString)
        .toScala
    } yield {
      println(parsed)
      assert(parsed.entries.size() == 1)
      assert(parsed.entries.get(0).getLevel == "Violation")
      assert(parsed.entries.get(0).getName == "validation1")
    }
  }

  test("parse best practices trace") {
    for {
      profileContent <- platform.fs.asyncFile(buildResourcePath("best-practices.yaml")).read()
      reportContent  <- platform.fs.asyncFile(buildResourcePath("best-practices.jsonld")).read()
      parsed <- AmfCustomValidationTools
        .parseReport(reportContent.toString, "file:///acv-test.raml", profileContent.toString)
        .toScala
    } yield {
      println(parsed)
      assert(parsed.entries.size() == 3)
      val entry = parsed.entries.asScala.find(c => c.name == "date-only-representation").get
      assert(entry.location.getUri == "file:///acv-test.raml")
      assert(entry.location.getRange == new Range(new Position(2, 6), new Position(3, 8)))
      assert(entry.trace.asScala.nonEmpty)
    }
  }
}
