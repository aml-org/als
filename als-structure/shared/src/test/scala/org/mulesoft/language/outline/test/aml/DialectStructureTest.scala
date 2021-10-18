package org.mulesoft.language.outline.test.aml

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.language.outline.test.BaseStructureTest
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class DialectStructureTest extends BaseStructureTest with DialectTest {

  def runTest(path: String, dialectPath: String, jsonPath: String): Future[Assertion] = {
    val fullDialectPath = filePath(dialectPath)
    for {
      amfConfiguration <- AmfConfigurationWrapper()
      parsed           <- amfConfiguration.parse(fullDialectPath)
      _ <- Future {
        parsed.result.baseUnit match {
          case d: Dialect =>
            amfConfiguration.registerDialect(d)
        }
        parsed
      }
      result <- super.runTest(path, jsonPath, Some(amfConfiguration))
    } yield result
  }

}
