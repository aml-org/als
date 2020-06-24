package org.mulesoft.language.outline.test.aml

import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.language.outline.test.BaseStructureTest
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class DialectStructureTest extends BaseStructureTest with DialectTest {

  def runTest(path: String, dialectPath: String, jsonPath: String): Future[Assertion] = {
    val fullDialectPath = filePath(dialectPath)
    val default         = AmfInstance.default
    default.parse(fullDialectPath).map(_.baseUnit).flatMap(_ => super.runTest(path, jsonPath, Some(default)))
  }

}
