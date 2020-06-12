package org.mulesoft.als.server.modules.rename.raml

class ServerRenameTests extends RAMLRenameTest {

  test("test 001") { runTest("test001/api.raml", "MyType2") }
  test("test 002") { runTest("test002/api.yaml", "RENAMED") }
}
