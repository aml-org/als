package org.mulesoft.als.server.modules.rename.jsonSchema

class JsonSchemaServerRenameTest extends JsonSchemaRenameTest {
  ignore("Test 001 - Rename security scheme referenced from sequence") {
    runTest("test.001/draft-03.propertyName.json", "RENAMED")
  }
}
