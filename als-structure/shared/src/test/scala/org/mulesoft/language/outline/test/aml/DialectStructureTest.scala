package org.mulesoft.language.outline.test.aml

import amf.internal.environment.Environment
import org.mulesoft.amfintegration.AmfInstance
import org.mulesoft.language.outline.test.BaseStructureTest
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class DialectStructureTest extends BaseStructureTest with DialectTest {

  def runTest(path: String, dialectPath: String, jsonPath: String): Future[Assertion] = {
    val fullDialectPath = filePath(dialectPath)
    AmfInstance(platform, Environment())
      .parse(fullDialectPath)
      .map(_.baseUnit)
      .flatMap(_ => super.runTest(path, jsonPath))
  }

}
