package org.mulesoft.language.outline.test.oas20.structure

import org.mulesoft.language.outline.test.oas20.OAS20Test

class StructureTests extends OAS20Test {

    override def rootPath: String = "OAS20/structure"

    test("test 001 YAML") {
        this.runTest("test001/api.yml", "test001/api-yaml-outline.json")
    }

    test("test 001 JSON") {
        this.runTest("test001/api.json", "test001/api-json-outline.json")
    }

    test("test 002 YAML") {
        this.runTest("test002/api.yml", "test002/api-yaml-outline.json")
    }

    test("test 002 JSON") {
        this.runTest("test002/api.json", "test002/api-json-outline.json")
    }

    test("test 003 YAML") {
        this.runTest("test003/api.yml", "test003/api-yaml-outline.json")
    }

    test("test 003 JSON") {
        this.runTest("test003/api.json", "test003/api-json-outline.json")
    }

    test("test 004 YAML") {
        this.runTest("test004/api.yml", "test004/api-yaml-outline.json")
    }

    test("test 004 JSON") {
        this.runTest("test004/api.json", "test004/api-json-outline.json")
    }

    test("test 005 YAML") {
        this.runTest("test005/api.yml", "test005/api-yaml-outline.json")
    }

    test("test 005 JSON") {
        this.runTest("test005/api.json", "test005/api-json-outline.json")
    }

    test("test 006 YAML") {
        this.runTest("test006/api.yml", "test006/api-yaml-outline.json")
    }

    test("test 006 JSON") {
        this.runTest("test006/api.json", "test006/api-json-outline.json")
    }

    test("test 007 YAML") {
        this.runTest("test007/api.yml", "test007/api-yaml-outline.json")
    }

    test("test 007 JSON") {
        this.runTest("test007/api.json", "test007/api-json-outline.json")
    }

    test("test 008 YAML") {
        this.runTest("test008/api.yml", "test008/api-yaml-outline.json")
    }

    test("test 008 JSON") {
        this.runTest("test008/api.json", "test008/api-json-outline.json")
    }

    test("test 009 YAML") {
        this.runTest("test009/api.yml", "test009/api-yaml-outline.json")
    }

    test("test 009 JSON") {
        this.runTest("test009/api.json", "test009/api-json-outline.json")
    }

    test("test 010 YAML") {
        this.runTest("test010/api.yml", "test010/api-yaml-outline.json")
    }

    test("test 010 JSON") {
        this.runTest("test010/api.json", "test010/api-json-outline.json")
    }
}