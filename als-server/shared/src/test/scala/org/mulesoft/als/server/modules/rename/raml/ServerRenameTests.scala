package org.mulesoft.als.server.modules.rename.raml

class ServerRenameTests extends RAMLRenameTest {

  test("test 001") { runTest("test001/api.raml", "MyType2") }
  test("test 002") { runTest("test002/api.yaml", "RENAMED") }
  test("test 003") { runTest("test003/api.yaml", "RENAMED") }
  test("test 004") { runTest("test004/api.yaml", "RENAMED") }
  test("test 005") { runTest("test005/api.yaml", "RENAMED") }
}
