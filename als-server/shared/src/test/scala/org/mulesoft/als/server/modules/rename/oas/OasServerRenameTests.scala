package org.mulesoft.als.server.modules.rename.oas

class OasServerRenameTests extends OASRenameTest {
  test("Test 001 - Rename security scheme referenced from sequence") {
    runTest("test001/securityRename.yaml", "RENAMED")
  }
}
