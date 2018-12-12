package org.mulesoft.high.level.test

import java.io.File

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}


trait AstTest extends AsyncFunSuite with PlatformSecrets{

    implicit override def executionContext:ExecutionContext =
        scala.concurrent.ExecutionContext.Implicits.global

    def runTest(path:String,test:IProject => Assertion):Future[Assertion] = {
        parse(filePath(path)).map(test)
    }

    def format:String

    def parse(path:String,env:Environment=Environment()): Future[IProject] = {

        parseAMF(path,env).flatMap(unit => Core.buildModel(unit,platform))
    }

    def parseAMF(path:String,env:Environment=Environment()): Future[BaseUnit] = {

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
            helper.parse(cfg,env))
    }

    def rootPath:String

    def filePath(path:String):String = {
        var rootDir = System.getProperty("user.dir")
        s"file://$rootDir/als-hl/shared/src/test/resources/$rootPath/$path".replace('\\','/')
    }

    def bulbLoaders(path: String, content:String) = {
        var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
            override def accepts(resource: String): Boolean = resource == path

            override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
        })
        loaders ++= platform.loaders()
        loaders
    }

}
