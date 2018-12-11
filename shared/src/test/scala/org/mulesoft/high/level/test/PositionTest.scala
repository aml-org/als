package org.mulesoft.high.level.test

import amf.client.remote.Content
import amf.core.remote.File
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.high.level.{ReferenceSearchResult, Search}
import org.mulesoft.high.level.interfaces.IProject
import org.scalatest.Assertion

import scala.concurrent.Future

trait PositionTest extends AstTest{

    protected var _position:Int = 0

    def position:Int = _position

    def runFindDefinitionTest(path:String,test:Option[ReferenceSearchResult] => Assertion):Future[Assertion] = {
        parse(filePath(path)).map(project =>
            Search.findDefinitionByPosition(
                project.rootASTUnit,position)).map(test)
    }

    def runFindReferencesTest(path:String,test:Option[ReferenceSearchResult] => Assertion):Future[Assertion] = {
        parse(filePath(path)).map(project =>
            Search.findReferencesByPosition(
                project.rootASTUnit,position)).map(test)
    }


    def parse(path:String): Future[IProject] = {

        platform.resolve(path).map(c => {
            var ci = getPositions(c.stream.toString)
            _position = ci.position
            var loaders: Seq[ResourceLoader] = bulbLoaders(path, ci.content)
            var env:Environment = Environment(loaders)
            env
        }).flatMap(env=>{
            super.parse(path,env)
        })
    }

    def label:String = "*"

    def getPositions(str: String): CompletionInput = {

        var position = str.indexOf(label)
        if (position < 0) {
            position = str.length
        }

        var content = str.substring(0, position)
        if (str.lengthCompare(position + 1) > 0) {
            content += str.substring(position + 1)
        }
        new CompletionInput(content, position)

    }

    def testFindDefinitionNegative(opt:Option[ReferenceSearchResult]):Assertion = {
        opt match {
            case Some(c) =>
                fail(s"No definition is expected to be found, but a ${c.definition.definition.nameId.get} is found")
            case None => succeed
        }
    }

    def testFindTemplateReferencesNegative(opt:Option[ReferenceSearchResult]):Assertion = {
        opt match {
            case Some(c) =>
                fail(s"No definition is expected to be found, but a ${c.definition.definition.nameId.get} is found")
            case None => succeed
        }
    }

}

class CompletionInput(val content:String, val position:Int) {}
