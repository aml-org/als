package org.mulesoft.language.outline.test.jsonschema.structure

import org.mulesoft.language.outline.test.jsonschema.JsonSchemaImplTest

class JsonSchemaStructureTest extends JsonSchemaImplTest {

  test("Draft-03 - Test 001 Json Schema") {
    this.runTest("draft-03/basic-schema.json", "draft-03/basic-schema-outline.json")
  }

  test("Draft-04 - Test 001 Json Schema") {
    this.runTest("draft-04/basic-schema.json", "draft-04/basic-schema-outline.json")
  }

  test("Draft-07 - Test 001 Json Schema") {
    this.runTest("draft-07/basic-schema.json", "draft-07/basic-schema-outline.json")
  }

  test("Draft-2019 - Test 001 Json Schema") {
    this.runTest("draft-2019-09/basic-schema.json", "draft-2019-09/basic-schema-outline.json")
  }
}