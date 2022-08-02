package org.mulesoft.als.server.modules.rename.jsonschema

class JsonSchemaServerRenameTests extends JsonSchemaRenameTest {

  // ignored until amf adds links instead of inlined references (W-11461036)
  ignore("Draft-03/Test 001 - Rename schema in definitions") {
    runTest("draft-03/test001/basic-schema.json", "RENAMED")
  }

  ignore("Draft-04/Test 001 - Rename schema in definitions") {
    runTest("draft-04/test001/basic-schema.json", "RENAMED")
  }

  ignore("Draft-07/Test 001 - Rename schema in definitions") {
    runTest("draft-07/test001/basic-schema.json", "RENAMED")
  }

  ignore("Draft-2019-09/Test 001 - Rename schema in definitions") {
    runTest("draft-2019-09/test001/basic-schema.json", "RENAMED")
  }
}
