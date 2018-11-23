package org.mulesoft.high.level.test.AML.AST

import org.mulesoft.high.level.interfaces.IAttribute
import org.mulesoft.high.level.test.AML.DialectASTTest

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class BasicLibraryTests extends DialectASTTest{
    override def rootPath: String = "AML/ASTTests"

    private def dialectPath = "AsyncAPI/dialect6.yaml"

    private def instancePath = "AsyncAPI/library.yaml"

    test("test 002. nested attribute value"){
        runTest( dialectPath, instancePath, project => {

            val expectedValue = "a message"
            project.rootASTUnit.rootNode.elements("messages").headOption.flatMap(_.attribute("summary")) match {
                case Some(a) => a.value match {
                    case Some(expectedValue) => succeed
                    case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
                }
                case _ => fail("'messages/a_message/summary' attribute not found")
            }
        })
    }


    test("test 003. elements collection"){
        runTest( dialectPath, instancePath, project => {

            val expectedValue:Int = 2
            val length = project.rootASTUnit.rootNode.elements("messages").headOption.get.element("payload").get.elements("properties").length
            if(length == expectedValue){
                succeed
            }
            else {
                fail(s"$expectedValue properties are expected while got $length")
            }
        })
    }


    test("test 005. mapKey attribute 2"){
        runTest( dialectPath, instancePath, project => {

            val expectedValue = "key1"
            project.rootASTUnit.rootNode.elements("messages").headOption.flatMap(_.elements("simpleMap").headOption).flatMap(_.attribute("key")) match {
                case Some(a) => a.value match {
                    case Some(expectedValue) => succeed
                    case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
                }
                case _ => fail(s"'simpleMap[$expectedValue]' mapKey attribute not found")
            }
        })
    }

    test("test 006. mapValue attribute 1"){
        runTest( dialectPath, instancePath, project => {

            val expectedValue = "value1"
            project.rootASTUnit.rootNode.elements("messages").headOption.flatMap(_.elements("simpleMap").headOption).flatMap(_.attribute("value")) match {
                case Some(a) => a.value match {
                    case Some(expectedValue) => succeed
                    case _ => fail(s"Expected value: $expectedValue, actual: ${a.value}")
                }
                case _ => fail(s"'simpleMap[key1]' mapValue attribute not found")
            }
        })
    }

    test("test 007. attributes collection 1"){
        runTest( dialectPath, instancePath, project => {

            val val0 = "string"
            val val1 = "number"

            project.rootASTUnit.rootNode.elements("messages").headOption.flatMap(_.element("payload")).flatMap(_.elements("properties").headOption).map(x => x.attributes("type")) match {
                case Some(attrs) => {
                    val l = attrs.lengthCompare(2)
                    if(l != 0){
                        fail(s"Expected 2 values, but got $l values")
                    }
                    else {
                        val list = ArrayBuffer[IAttribute]() ++= attrs
                        val attr0 = list(0)
                        val attr1 = list(1)
                        if (attr0.value.isEmpty) {
                            fail(s"value is missing for messages/a_message/payload/type[0]")
                        }
                        else if (attr1.value.isEmpty) {
                            fail(s"value is missing for messages/a_message/payload/type[1]")
                        }
                        else if (!attr0.value.contains(val0)) {
                            fail(s"at messages/a_message/payload/type[0] expecting $val0 but got ${attr0.value.get}")
                        }
                        else if (!attr1.value.contains(val1)) {
                            fail(s"at messages/a_message/payload/type[1] expecting $val1 but got ${attr1.value.get}")
                        }
                        else {
                            succeed
                        }
                    }
                }
                case _ => fail(s"'messages/a_message/payload/type attributes not found")
            }
        })
    }
}
