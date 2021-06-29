package org.mulesoft.language.outline.test.aml

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.language.outline.test.BaseStructureTest
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class DialectStructureTest extends BaseStructureTest with DialectTest {

  def runTest(path: String, dialectPath: String, jsonPath: String): Future[Assertion] = {
    val fullDialectPath  = filePath(dialectPath)
    val amfConfiguration = AmfConfigurationWrapper()
    amfConfiguration
      .parse(fullDialectPath)
      .map { r =>
        r.result.baseUnit match {
          case d: Dialect =>
            amfConfiguration.registerDialect(d)
        }
        r
      }
      .flatMap(_ => super.runTest(path, jsonPath, Some(amfConfiguration)))
  }

}
