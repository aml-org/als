package org.mulesoft.high.level.test.AML

import amf.core.client.ParserConfig
import amf.internal.environment.Environment
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.high.level.test.{AstTest, ParserHelper}
import java.io.File

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.remote.{Aml, JvmPlatform}
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}

abstract class DialectASTTest extends AstTest{

    def format:String = Aml.toString

    def runTest(dialectPath:String,dialectInstancePath:String,test:IProject => Assertion):Future[Assertion] = {
        parseAMF(filePath(dialectPath)).flatMap(_=>parse(filePath(dialectInstancePath))).map(test)
    }

}
