package org.mulesoft.language.outline.test.aml

import org.mulesoft.language.outline.test.BaseStructureTest
import org.mulesoft.lsp.server.DefaultAmfConfiguration
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class DialectStructureTest extends BaseStructureTest with DialectTest {

  def runTest(path: String, dialectPath: String, jsonPath: String): Future[Assertion] = {
    val fullDialectPath = filePath(dialectPath)
    DefaultAmfConfiguration.parse(fullDialectPath).map(_.baseUnit).flatMap(_ => super.runTest(path, jsonPath))
  }

}
