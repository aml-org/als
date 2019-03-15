package org.mulesoft.language.outline.test.aml

import amf.internal.environment.Environment
import org.mulesoft.language.outline.test.StructureTest
import org.scalatest.compatible.Assertion

import scala.concurrent.Future

class DialectStructureTest extends StructureTest with DialectTest {

    def runTest(path:String, dialectPath:String, jsonPath:String):Future[Assertion] = {

        val fullDialectPath = filePath(dialectPath)
        val config = this.buildParserConfig(format, fullDialectPath)
        this.amfParse(config).flatMap(_=>super.runTest(path,jsonPath))
    }

}
