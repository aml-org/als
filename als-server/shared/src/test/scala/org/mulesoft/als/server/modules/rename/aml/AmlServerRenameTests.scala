package org.mulesoft.als.server.modules.rename.aml

class AmlServerRenameTests extends AMLRenameTest {

  test("test 001") { runTest("extensions/test001/extension.yaml", "RENAMED") }
  ignore("test 002") { runTest("extensions/test002/extension.yaml", "RENAMED") }
}