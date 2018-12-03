package org.mulesoft.high.level.test

import java.io.File

import amf.core.client.ParserConfig
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode, IParseResult, IProject}
import org.mulesoft.typesystem.json.interfaces.JSONWrapper
import org.mulesoft.typesystem.json.interfaces.JSONWrapperKind._
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}


trait ASTEditingTest extends AstTest{

    def runAttributeEditingTest(path:String,astExtractor:IProject => Option[IAttribute], value:Any):Future[Assertion] = runAttributeEditingTestInternal(path,astExtractor,value).map(_.result)

    def runAttributeEditingTestInternal(path:String,astExtractor:IProject => Option[IAttribute], value:Any):Future[TestResult] = {
        val fp:String = filePath(path)
        parse(fp).flatMap(project=>runAttributeEditingTest1Internal(project,astExtractor, value))
    }

    def runAttributeEditingTest1(project:IProject,astExtractor:IProject => Option[IAttribute], value:Any):Future[Assertion] = runAttributeEditingTest1Internal(project,astExtractor, value).map(_.result)

    def runAttributeEditingTest1Internal(project:IProject,astExtractor:IProject => Option[IAttribute], value:Any):Future[TestResult] = {
        val fp: String = project.rootASTUnit.path
        var attrOpt = astExtractor(project)
        attrOpt.get.setValue(value).flatMap(modificationResult => {
            var modifiedContent = modificationResult.content
            var loaders = bulbLoaders(fp, modifiedContent)
            var env = Environment(loaders)
            super.parse(fp, env)
        }).map(modifiedProject => {
            var modifiedAttrOpt = astExtractor(modifiedProject)
            modifiedAttrOpt.get.value match {
                case Some(v) =>
                    var aVal = v
                    if(v.isInstanceOf[JSONWrapper]){
                        var jw = v.asInstanceOf[JSONWrapper]
                        value match {
                            case str:String => aVal = jw.value(STRING).orNull
                            case b:Boolean => aVal = jw.value(BOOLEAN).orNull
                            case b:Number => aVal = jw.value(NUMBER).orNull
                            case null =>  aVal = jw.value(NULL).get
                            case _ =>
                        }
                    }
                    if(aVal == value){
                        TestResult(succeed,modifiedProject)
                    }
                    else {
                        TestResult(fail(s"expected '$value' but got '$aVal'"),modifiedProject)
                    }
                case _ => TestResult(fail("there is no value"),modifiedProject)
            }
        })
    }

    def runAttributeCreationTest(path:String, astExtractor:IProject => Option[IHighLevelNode], propName:String, value:Any):Future[Assertion] = runAttributeCreationTestInternal(path,astExtractor,propName,value).map(_.result)


    def runAttributeCreationTestInternal(path:String, astExtractor:IProject => Option[IHighLevelNode], propName:String, value:Any):Future[TestResult] = {
        val fp:String = filePath(path)
        parse(fp).flatMap(project=>runAttributeCreationTest1Internal(project,astExtractor, propName, value))
    }

    def runAttributeCreationTest1(project:IProject, astExtractor:IProject => Option[IHighLevelNode], propName:String, value:Any):Future[Assertion] =
        runAttributeCreationTest1Internal(project,astExtractor, propName, value).map(_.result)

    def runAttributeCreationTest1Internal(project:IProject, astExtractor:IProject => Option[IHighLevelNode], propName:String, value:Any):Future[TestResult] = {

        val fp:String = project.rootASTUnit.path
        var elementOpt = astExtractor(project)
        var element = elementOpt.get
        var prop = element.definition.property(propName).get
        element.newChild(prop).map(x => x.asAttr.get.setValue(value)).get.flatMap(modificationResult => {
            var modifiedContent = modificationResult.content
            var loaders = bulbLoaders(fp, modifiedContent)
            var env = Environment(loaders)
            super.parse(fp, env)
        }).map(modifiedProject => {
            var elementOpt = astExtractor(modifiedProject)
            elementOpt.get.attribute(propName).get.value match {
                case Some(v) =>
                    var aVal = v
                    if(v.isInstanceOf[JSONWrapper]){
                        var jw = v.asInstanceOf[JSONWrapper]
                        value match {
                            case str:String => aVal = jw.value(STRING).orNull
                            case b:Boolean => aVal = jw.value(BOOLEAN).orNull
                            case b:Number => aVal = jw.value(NUMBER).orNull
                            case null =>  aVal = jw.value(NULL).get
                            case _ =>
                        }
                    }
                    if(aVal == value){
                        TestResult(succeed,modifiedProject)
                    }
                    else {
                        TestResult(fail(s"expected '$value' but got '$aVal'"),modifiedProject)
                    }
                case _ => TestResult(fail("there is no value"),modifiedProject)
            }
        })
    }
}

class TestResult(val result:Assertion, val modifiedProject:IProject){}

object TestResult {

    def apply(result: Assertion,modifiedProject: IProject): TestResult = new TestResult(result,modifiedProject)
}