package org.mulesoft.als.server.modules.rename.raml

class RamlServerRenameTests extends RAMLRenameTest {

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
  test("test 016 - RAML type expressions union") { runTest("test016/api.raml", "RENAMED") }
  test("test 017 - RAML type expressions array") { runTest("test017/api.raml", "RENAMED") }
  test("test 018 - RAML type expressions") { runTest("test018/api.raml", "RENAMED") }
  test("test 019 - Rename without references") { runTest("test019/api.raml", "RENAMED") }
  ignore("test 020 - Rename through reference") { runTest("test020/api.raml", "RENAMED") }
  test("test 021 - JSON") { runTest("test021/api.json", "RENAMED") }
  test("test 022 - RAML Expression with union") { runTest("test022/api.raml", "RENAMED") }
  test("test 023 - RAML Expression with triple union") { runTest("test023/api.raml", "RENAMED") }
  test("test 024 - RAML Expression with simple array") { runTest("test024/api.raml", "RENAMED") }
  test("test 025 - RAML Expression with union array") { runTest("test025/api.raml", "RENAMED") }
  test("test 026 - RAML should be able to on the value") { runTestDisabled("test026/api.raml") }
}