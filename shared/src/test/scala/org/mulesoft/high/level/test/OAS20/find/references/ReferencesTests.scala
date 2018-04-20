package org.mulesoft.high.level.test.OAS20.find.references

import org.mulesoft.high.level.ReferenceSearchResult
import org.mulesoft.high.level.test.OAS20.OASFindReferencesTest
import org.scalatest.Assertion

class ReferencesTests extends OASFindReferencesTest{

    test("Schema type references test 1 JSON. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindReferencesTest("definitions/test001/spec.json", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Schema type references test 1 YAML. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindReferencesTest("definitions/test001/spec.yml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Schema type references test 2 JSON. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindReferencesTest("definitions/test002/spec.json", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Schema type references test 2 YAML. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindReferencesTest("definitions/test002/spec.yml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Schema type references test 3 JSON. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindReferencesTest("definitions/test003/spec.json", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Schema type references test 3 YAML. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindReferencesTest("definitions/test003/spec.yml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Schema type references test 4 JSON. Negative") {
        runFindReferencesTest("definitions/test004/spec.json", x => testFindTemplateReferencesNegative(x))
    }

    test("Schema type references test 4 YAML. Negative") {
        runFindReferencesTest("definitions/test004/spec.yml", x => testFindTemplateReferencesNegative(x))
    }

    test("Schema type references test 5 JSON. Negative") {
        runFindReferencesTest("definitions/test005/spec.json", x => testFindTemplateReferencesNegative(x))
    }

    test("Schema type references test 5 YAML. Negative") {
        runFindReferencesTest("definitions/test005/spec.yml", x => testFindTemplateReferencesNegative(x))
    }

    test("Schema type references test 6 JSON. Negative") {
        runFindReferencesTest("definitions/test006/spec.json", x => testFindTemplateReferencesNegative(x))
    }

    test("Schema type references test 6 YAML. Negative") {
        runFindReferencesTest("definitions/test006/spec.yml", x => testFindTemplateReferencesNegative(x))
    }

//    test("Parameter type references test 1 JSON. Positive") {
//        var templateName = "param2"
//        var templateType = "ParameterObject"
//        runFindReferencesTest("parameters/test001/spec.json", x => testFindReferencesPositive(x,templateType,templateName,3))
//    }
//
//    test("Parameter references test 1 YAML. Positive") {
//        var templateName = "param2"
//        var templateType = "ParameterObject"
//        runFindReferencesTest("parameters/test001/spec.yml", x => testFindReferencesPositive(x,templateType,templateName,3))
//    }
//
//    test("Response type references test 1 JSON. Positive") {
//        var templateName = "r2"
//        var templateType = "ResponseObject"
//        runFindReferencesTest("responses/test001/spec.json", x => testFindReferencesPositive(x,templateType,templateName,3,"key"))
//    }
//
//    test("Response references test 1 YAML. Positive") {
//        var templateName = "r2"
//        var templateType = "ResponseObject"
//        runFindReferencesTest("responses/test001/spec.yml", x => testFindReferencesPositive(x,templateType,templateName,3,"key"))
//    }

    def testFindReferencesPositive(opt:Option[ReferenceSearchResult], typeName:String, name:String, refsCount:Int,attrName:String="name"):Assertion = {
        opt match {
            case Some(c) =>
                c.definition.attribute(attrName) match {
                    case Some(a) => a.value match {
                        case Some(aVal) =>
                            if(aVal != name){
                                fail(s"ExpectedName: $name, actual: $aVal")
                            }
                            else {
                                val definition = c.definition.definition
                                if (!definition.isAssignableFrom(typeName)) {
                                    fail(s"$typeName is expected but found  ${definition.nameId.get}")
                                }
                                else if(c.references.lengthCompare(refsCount)!=0){
                                    fail(s"References array length is expected to be $refsCount but got ${c.references.length}")
                                }
                                else {
                                    succeed
                                }
                            }
                        case _ => fail(s"'$attrName' attribute has no value")
                    }

                    case _ => fail("'name' attribute not found")
                }
            case None => fail("Search result is empty")
        }
    }
}

