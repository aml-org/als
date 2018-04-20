package org.mulesoft.high.level.test

import amf.core.remote.File
import org.mulesoft.high.level.{ReferenceSearchResult, Search}
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.test.MainCompletion.{getPositions, platform}
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


    override def parse(path:String): Future[IProject] = {

        platform.resolve(path,None).map(c => {
            var ci = getPositions(c.stream.toString)
            _position = ci.position
            File.unapply(path).foreach(x=>platform.cacheResourceText(x, ci.content))
        }).flatMap(x=>{
            super.parse(path)
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
