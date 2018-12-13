package org.mulesoft.language.outline.test.raml10.structure

import org.mulesoft.language.outline.test.raml10.RAML10StructureTest


class StructureTests extends RAML10StructureTest {

    override def rootPath:String = "RAML10/structure"

    test("test 001") {
        this.runTest("test001/api.raml", "test001/api-outline.json")
    }

    test("test 002") {
        this.runTest("test002/api.raml", "test002/api-outline.json")
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

    test("test 008") {
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

    test("test 026") {
        this.runTest("test026/overlay.raml", "test026/api-outline.json")
    }

    test("test 027") {
        this.runTest("test027/extension.raml", "test027/api-outline.json")
    }
}
