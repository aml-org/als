package org.mulesoft.als.server.modules.rename.jsonschema

class JsonSchemaServerRenameTests extends JsonSchemaRenameTest {
  // ignored until amf adds links instead of inlined references (W-11461036)
  ignore("Test 001 - Rename schema in definitions") {
    runTest("test001/basic-schema.json", "RENAMED")
  }
}
