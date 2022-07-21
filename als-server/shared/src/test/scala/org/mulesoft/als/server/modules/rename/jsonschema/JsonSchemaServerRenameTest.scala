package org.mulesoft.als.server.modules.rename.jsonschema

class JsonSchemaServerRenameTest extends JsonSchemaRenameTest {
  ignore("Test 001 - Rename security scheme referenced from sequence") {
    runTest("test001/basic-schema.json", "RENAMED")
  }
}
