package org.mulesoft.language.outline.test.jsonschema.draft03.structure

import org.mulesoft.language.outline.test.jsonschema.draft03.JsonSchemaDraft03StructureTest

class JsonSchemaStructureTest extends JsonSchemaDraft03StructureTest {

  test("test 001 Json Schema") {
    this.runTest("draft-04/basics-schema.json", "draft-04/basics-schema-outline.json")
  }}
