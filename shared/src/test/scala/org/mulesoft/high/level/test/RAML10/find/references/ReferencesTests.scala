package org.mulesoft.high.level.test.RAML10.find.references

import org.mulesoft.high.level.ReferenceSearchResult
import org.mulesoft.high.level.test.RAML10.RamlFindReferencesTest
import org.scalatest.Assertion

class ReferencesTests extends RamlFindReferencesTest{

    test("Resource type references test 1. Positive") {
        var templateName = "rt"
        var templateType = "ResourceType"
        runFindReferencesTest("resourceTypes/test001/api.raml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Resource type references test 2. Positive") {
        var templateName = "rt"
        var templateType = "ResourceType"
        runFindReferencesTest("resourceTypes/test002/api.raml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Resource type references test 3. Positive") {
        var templateName = "rt"
        var templateType = "ResourceType"
        runFindReferencesTest("resourceTypes/test003/api.raml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Resource type references test 4. Negative") {
        runFindReferencesTest("resourceTypes/test004/api.raml", x => testFindTemplateReferencesNegative(x))
    }

    test("Resource type references test 5. Negative") {
        runFindReferencesTest("resourceTypes/test005/api.raml", x => testFindTemplateReferencesNegative(x))
    }

    test("Trait references test 1. Positive") {
        var templateName = "tr"
        var templateType = "Trait"
        runFindReferencesTest("traits/test001/api.raml", x => testFindReferencesPositive(x,templateType,templateName,4))
    }

    test("Trait references test 2. Positive") {
        var templateName = "tr"
        var templateType = "Trait"
        runFindReferencesTest("traits/test002/api.raml", x => testFindReferencesPositive(x,templateType,templateName,4))
    }

    test("Trait references test 3. Positive") {
        var templateName = "tr"
        var templateType = "Trait"
        runFindReferencesTest("traits/test003/api.raml", x => testFindReferencesPositive(x,templateType,templateName,4))
    }

    test("Trait references test 4. Negative") {
        runFindReferencesTest("traits/test004/api.raml", x => testFindTemplateReferencesNegative(x))
    }

    test("Trait references test 5. Negative") {
        runFindReferencesTest("traits/test005/api.raml", x => testFindTemplateReferencesNegative(x))
    }

    test("Type references test 1. Positive") {
        var templateName = "t1"
        var templateType = "ObjectTypeDeclaration"
        runFindReferencesTest("types/test001/api.raml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Type references test 2. Positive") {
        var templateName = "t1"
        var templateType = "ObjectTypeDeclaration"
        runFindReferencesTest("types/test002/api.raml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }

    test("Type references test 3. Positive") {
        var templateName = "t1"
        var templateType = "ObjectTypeDeclaration"
        runFindReferencesTest("types/test003/api.raml", x => testFindReferencesPositive(x,templateType,templateName,2))
    }




    def testFindReferencesPositive(opt:Option[ReferenceSearchResult], typeName:String, name:String, refsCount:Int):Assertion = {
        opt match {
            case Some(c) =>
                c.definition.attribute("name") match {
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
                        case _ => fail("'name' attribute has no value")
                    }

                    case _ => fail("'name' attribute not found")
                }
            case None => fail("Search result is empty")
        }
    }
}

