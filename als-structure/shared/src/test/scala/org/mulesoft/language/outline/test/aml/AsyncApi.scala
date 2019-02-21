package org.mulesoft.language.outline.test.aml

class AsyncApi extends DialectStructureTest {

    test("Async API dialect instance 001") {
        this.runTest("AsyncAPI/example6.yaml", "AsyncAPI/dialect6.yaml", "AsyncAPI/example6.json")
    }

    test("Async API dialect library 001") {
        this.runTest("AsyncAPI/library.yaml", "AsyncAPI/dialect6.yaml", "AsyncAPI/library.json")
    }

    test("Async API dialect fragment 001") {
        this.runTest("AsyncAPI/fragment.yaml", "AsyncAPI/dialect6.yaml", "AsyncAPI/fragment.json")
    }
}
