package org.mulesoft.high.level.test.OAS20.find.definition

import org.mulesoft.high.level.ReferenceSearchResult
import org.mulesoft.high.level.test.OAS20.OASFindDefinitionTest
import org.scalatest.Assertion

class DefinitionTests extends OASFindDefinitionTest{

    test("Schema type definition test 1 JSON. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindDefinitionTest("definitions/test001/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Schema type definition test 1 YAML. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindDefinitionTest("definitions/test001/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Schema type definition test 2 JSON. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindDefinitionTest("definitions/test002/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Schema type definition test 2 YAML. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindDefinitionTest("definitions/test002/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Schema type definition test 3 JSON. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindDefinitionTest("definitions/test003/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Schema type definition test 3 YAML. Positive") {
        var templateName = "T2"
        var templateType = "SchemaObject"
        runFindDefinitionTest("definitions/test003/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Schema type definition test 4 JSON. Negative") {
        runFindDefinitionTest("definitions/test004/spec.json", x => testFindDefinitionNegative(x))
    }

    test("Schema type definition test 4 YAML. Negative") {
        runFindDefinitionTest("definitions/test004/spec.yml", x => testFindDefinitionNegative(x))
    }

    test("Schema type definition test 5 JSON. Negative") {
        runFindDefinitionTest("definitions/test005/spec.json", x => testFindDefinitionNegative(x))
    }

    test("Schema type definition test 5 YAML. Negative") {
        runFindDefinitionTest("definitions/test005/spec.yml", x => testFindDefinitionNegative(x))
    }

    test("Schema type definition test 6 JSON. Negative") {
        runFindDefinitionTest("definitions/test006/spec.json", x => testFindDefinitionNegative(x))
    }

    test("Schema type definition test 6 YAML. Negative") {
        runFindDefinitionTest("definitions/test006/spec.yml", x => testFindDefinitionNegative(x))
    }

    test("Parameter definition test 1 JSON. Positive") {
        var templateName = "p2"
        var templateType = "ParameterObject"
        runFindDefinitionTest("parameters/test001/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Parameter definition test 1 YAML. Positive") {
        var templateName = "p2"
        var templateType = "ParameterObject"
        runFindDefinitionTest("parameters/test001/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Parameter definition test 2 JSON. Positive") {
        var templateName = "p2"
        var templateType = "ParameterObject"
        runFindDefinitionTest("parameters/test002/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Parameter definition test 2 YAML. Positive") {
        var templateName = "p2"
        var templateType = "ParameterObject"
        runFindDefinitionTest("parameters/test002/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Parameter definition test 3 JSON. Positive") {
        var templateName = "p2"
        var templateType = "ParameterObject"
        runFindDefinitionTest("parameters/test003/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Parameter definition test 3 YAML. Positive") {
        var templateName = "p2"
        var templateType = "ParameterObject"
        runFindDefinitionTest("parameters/test003/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Parameter definition test 4 JSON. Negative") {
        runFindDefinitionTest("parameters/test004/spec.json", x => testFindDefinitionNegative(x))
    }

    test("Parameter definition test 4 YAML. Negative") {
        runFindDefinitionTest("parameters/test004/spec.yml", x => testFindDefinitionNegative(x))
    }

    test("Parameter definition test 5 JSON. Negative") {
        runFindDefinitionTest("parameters/test005/spec.json", x => testFindDefinitionNegative(x))
    }

    test("Parameter definition test 5 YAML. Negative") {
        runFindDefinitionTest("parameters/test005/spec.yml", x => testFindDefinitionNegative(x))
    }

    test("Parameter definition test 6 JSON. Negative") {
        runFindDefinitionTest("parameters/test006/spec.json", x => testFindDefinitionNegative(x))
    }

    test("Parameter definition test 6 YAML. Negative") {
        runFindDefinitionTest("parameters/test006/spec.yml", x => testFindDefinitionNegative(x))
    }

    test("Response definition test 1 JSON. Positive") {
        var templateName = "r2"
        var templateType = "ResponseObject"
        runFindDefinitionTest("responses/test001/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Response definition test 1 YAML. Positive") {
        var templateName = "r2"
        var templateType = "ResponseObject"
        runFindDefinitionTest("responses/test001/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Response definition test 2 JSON. Positive") {
        var templateName = "r2"
        var templateType = "ResponseObject"
        runFindDefinitionTest("responses/test002/spec.json", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    test("Response definition test 2 YAML. Positive") {
        var templateName = "r2"
        var templateType = "ResponseObject"
        runFindDefinitionTest("responses/test002/spec.yml", x => testFindDefinitionPositive(x,templateType,templateName))
    }

    def testFindDefinitionPositive(opt:Option[ReferenceSearchResult], typeName:String, name:String):Assertion = {
        opt match {
            case Some(c) =>
                var keyAttrName = "key"
                if(c.definition.definition.nameId.contains("DefinitionObject")){
                    keyAttrName = "name"
                }
                c.definition.attribute(keyAttrName) match {
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
                                else if(c.references.isEmpty){
                                    fail("References array is empty")
                                }
                                else {
                                    succeed
                                }
                            }
                        case _ => fail("'name' attribute has no value")
                    }

                    case _ => fail("'name' attribute not found")
                }
            case None => fail("Search result is empty")
        }
    }
}
