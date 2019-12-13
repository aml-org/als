package org.mulesoft.language.outline.test.aml

import org.mulesoft.language.outline.test.BaseStructureTest
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class DialectStructureTest extends BaseStructureTest with DialectTest {

  def runTest(path: String, dialectPath: String, jsonPath: String): Future[Assertion] = {

    val fullDialectPath = filePath(dialectPath)
    this.amfParse(fullDialectPath).flatMap(_ => super.runTest(path, jsonPath))
  }

}
