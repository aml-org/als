package org.mulesoft.language.outline.test.raml10.structure

import org.mulesoft.language.outline.test.BaseStructureTest
import org.mulesoft.language.outline.test.raml10.RAML10Test

class RAML10StructureTests extends BaseStructureTest with RAML10Test {

  test("test 001") {
    this.runTest("test001/api.raml", "test001/api-outline.json")
  }

  test("test 002") {
    this.runTest("test002/api.raml", "test002/api-outline.json")
  }

  test("test 003") {
    this.runTest("test003/api.raml", "test003/api-outline.json")
  }

  test("test 004") {
    this.runTest("test004/api.raml", "test004/api-outline.json")
  }

  test("test 006") {
    this.runTest("test006/api.raml", "test006/api-outline.json")
  }

  test("test 007") {
    this.runTest("test007/api.raml", "test007/api-outline.json")
  }

  ignore("test 008") {
    this.runTest("test008/api.raml", "test008/api-outline.json")
  }

  test("test 009") {
    this.runTest("test009/api.raml", "test009/api-outline.json")
  }

  test("test 010") {
    this.runTest("test010/api.raml", "test010/api-outline.json")
  }

  test("test 011") {
    this.runTest("test011/api.raml", "test011/api-outline.json")
  }

  test("test 012") {
    this.runTest("test012/api.raml", "test012/api-outline.json")
  }

  test("test 013") {
    this.runTest("test013/api.raml", "test013/api-outline.json")
  }

  test("test 014") {
    this.runTest("test014/api.raml", "test014/api-outline.json")
  }

  ignore("test 015") {
    this.runTest("test015/api.raml", "test015/api-outline.json")
  }

  ignore("test 016") {
    this.runTest("test016/api.raml", "test016/api-outline.json")
  }

  test("test 017") {
    this.runTest("test017/api.raml", "test017/api-outline.json")
  }

  test("test 018") {
    this.runTest("test018/api.raml", "test018/api-outline.json")
  }

  test("test 019") {
    this.runTest("test019/api.raml", "test019/api-outline.json")
  }

  test("test 020") {
    this.runTest("test020/api.raml", "test020/api-outline.json")
  }

  test("test 021") {
    this.runTest("test021/api.raml", "test021/api-outline.json")
  }

  test("test 022") {
    this.runTest("test022/api.raml", "test022/api-outline.json")
  }

  test("test 023") {
    this.runTest("test023/api.raml", "test023/api-outline.json")
  }

  test("test 024") {
    this.runTest("test024/api.raml", "test024/api-outline.json")
  }

  test("test 025") {
    this.runTest("test025/api.raml", "test025/api-outline.json")
  }

  ignore("test 026") {
    this.runTest("test026/overlay.raml", "test026/api-outline.json")
  }

  test("test 027") {
    this.runTest("test027/extension.raml", "test027/api-outline.json")
  }

  test("test 028 - Complex structure") {
    this.runTest("test028/api.raml", "test028/api-outline.json")
  }

  test("String union nil range property: string?") {
    this.runTest("optional-string/api.raml", "optional-string/api-outline.json")
  }

  test("Nested files (inlined)") {
    this.runTest("nested-files/api.raml", "nested-files/api-outline.json")
  }

  test("Nested files types (inlined)") {
    this.runTest("nested-types/api.raml", "nested-types/api-outline.json")
  }

  test("Nested files traits (inlined)") {
    this.runTest("nested-traits/api.raml", "nested-traits/api-outline.json")
  }

  test("Test root field structure") {
    this.runTest("root/api.raml", "root/api-outline.json")
  }

  test("Test trait reference") {
    this.runTest("traits/api.raml", "traits/api-outline.json")
  }

  test("Test declaration keys range") {
    this.runTest("declarations/api.raml", "declarations/api-outline.json")
  }

  test("Test included example entry is present") {
    this.runTest("included-example/api.raml", "included-example/structure.json")
  }

  test("Test variable node is not present") {
    this.runTest("custom/no-variable.raml", "custom/no-variable-outline.json")
  }

  test("Test avoid duplicated is") {
    this.runTest("avoid-duplicated/is-in-resourcetype.raml", "avoid-duplicated/is-in-resourcetype-outline.json")
  }

  test("Test avoid duplicated enum") {
    this.runTest("avoid-duplicated/enum-in-resourcetype.raml", "avoid-duplicated/enum-in-resourcetype-outline.json")
  }

  test("Test avoid duplicated optional property") {
    this.runTest("avoid-duplicated/optional-property.raml", "avoid-duplicated/optional-property-outline.json")
  }
}
