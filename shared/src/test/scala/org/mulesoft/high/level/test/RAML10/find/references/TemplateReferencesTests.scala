package org.mulesoft.high.level.test.RAML10.find.references

import org.mulesoft.high.level.ReferenceSearchResult
import org.mulesoft.high.level.test.RAML10.RamlFindReferencesTest
import org.scalatest.Assertion

class TemplateReferencesTests extends RamlFindReferencesTest{

    test("Resource type references test 1. Positive") {
        var templateName = "rt"
        var templateType = "ResourceType"
        runFindReferencesTest("resourceTypes/test001/api.raml", x => testFindTemplateReferencesPositive(x,templateType,templateName,2))
    }

    test("Resource type references test 2. Positive") {
        var templateName = "rt"
        var templateType = "ResourceType"
        runFindReferencesTest("resourceTypes/test002/api.raml", x => testFindTemplateReferencesPositive(x,templateType,templateName,2))
    }

    test("Resource type references test 3. Positive") {
        var templateName = "rt"
        var templateType = "ResourceType"
        runFindReferencesTest("resourceTypes/test003/api.raml", x => testFindTemplateReferencesPositive(x,templateType,templateName,2))
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
        runFindReferencesTest("traits/test001/api.raml", x => testFindTemplateReferencesPositive(x,templateType,templateName,4))
    }

    test("Trait references test 2. Positive") {
        var templateName = "tr"
        var templateType = "Trait"
        runFindReferencesTest("traits/test002/api.raml", x => testFindTemplateReferencesPositive(x,templateType,templateName,4))
    }

    test("Trait references test 3. Positive") {
        var templateName = "tr"
        var templateType = "Trait"
        runFindReferencesTest("traits/test003/api.raml", x => testFindTemplateReferencesPositive(x,templateType,templateName,4))
    }

    test("Trait references test 4. Negative") {
        runFindReferencesTest("traits/test004/api.raml", x => testFindTemplateReferencesNegative(x))
    }

    test("Trait references test 5. Negative") {
        runFindReferencesTest("traits/test005/api.raml", x => testFindTemplateReferencesNegative(x))
    }




    def testFindTemplateReferencesPositive(opt:Option[ReferenceSearchResult], templateType:String, templateName:String, refsCount:Int):Assertion = {
        opt match {
            case Some(c) =>
                c.definition.attribute("name") match {
                    case Some(a) => a.value match {
                        case Some(aVal) =>
                            if(aVal != templateName){
                                fail(s"ExpectedName: $templateName, actual:$aVal")
                            }
                            else {
                                val definition = c.definition.definition
                                if (!definition.nameId.contains(templateType)) {
                                    fail(s"$templateType is expected but found  ${definition.nameId.get}")
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

    def testFindTemplateReferencesNegative(opt:Option[ReferenceSearchResult]):Assertion = {
        opt match {
            case Some(c) =>
                fail(s"No definition is expected to be found, but a ${c.definition.definition.nameId.get} is found")
            case None => succeed
        }
    }

}

