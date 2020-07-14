package org.mulesoft.als.server.modules.rename.raml

class ServerRenameTests extends RAMLRenameTest {

  test("test 001") { runTest("test001/api.raml", "MyType2") }
  test("test 002") { runTest("test002/api.yaml", "RENAMED") }
  test("test 003") { runTest("test003/api.yaml", "RENAMED") }
  test("test 004") { runTest("test004/api.yaml", "RENAMED") }
  test("test 005") { runTest("test005/api.yaml", "RENAMED") }
  test("test 006") { runTest("test006/api.raml", "RENAMED") }
  test("test 007") { runTest("test007/api.raml", "RENAMED") }
  test("test 008") { runTest("test008/api.raml", "RENAMED") }
  test("test 009") { runTest("test009/api.raml", "RENAMED") }
  test("test 010") { runTest("test010/api.raml", "RENAMED") }
  test("test 011") { runTest("test011/api.raml", "RENAMED") }
  test("test 012") { runTest("test012/api.raml", "RENAMED") }
  test("test 013") { runTest("test013/api.raml", "RENAMED") }
  test("test 014") { runTest("test014/api.raml", "RENAMED") }
  test("test 015") { runTest("test015/api.raml", "RENAMED") }
}