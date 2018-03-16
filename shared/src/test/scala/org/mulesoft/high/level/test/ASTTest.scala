package org.mulesoft.high.level.test

import java.io.File

import amf.core.client.ParserConfig
import amf.core.remote.JvmPlatform
import amf.core.unsafe.PlatformSecrets
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}


trait AstTest extends AsyncFunSuite with PlatformSecrets{

    def runTest(path:String,test:IProject => Assertion):Future[Assertion] = {
        parse(filePath(path)).map(test)
    }

    def format:String

    def parse(path:String): Future[IProject] = {

        var cfg = new ParserConfig(
            Some(ParserConfig.PARSE),
            Some(path),
            Some(format),
            Some("application/yaml"),
            None,
            Some("AMF Graph"),
            Some("application/ld+json")
        )

        val helper = ParserHelper(platform)
        Core.init().flatMap(_=>
            helper.parse(cfg).flatMap(unit =>
                Core.buildModel(unit,platform)))
    }

    def rootPath:String

    def filePath(path:String):String = {
        var rootDir = System.getProperty("user.dir")
        s"file://$rootDir/shared/src/test/resources/$rootPath/$path".replace('\\','/')
    }

}
