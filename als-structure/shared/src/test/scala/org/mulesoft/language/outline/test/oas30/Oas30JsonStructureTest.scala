package org.mulesoft.language.outline.test.oas30

import org.mulesoft.language.outline.test.BaseStructureTest

class Oas30JsonStructureTest extends BaseStructureTest {

  override def rootPath: String = "OAS30"

  test("test root-level") {
    forDir("root-level")
  }

  test("test root-level/info") {
    forDir("root-level/info")
  }

  test("test root-level/server") {
    forDir("root-level/server")
  }

  test("test components") {
    forDir("components")
  }

  test("test paths") {
    forDir("paths")
  }

  test("test paths/path-object") {
    forDir("paths/path-object")
  }

  test("test operation") {
    forDir("operation")
  }

  test("test operation/security") {
    forDir("operation/security")
  }

  test("test callbacks") {
    forDir("callbacks")
  }

  test("test request-body") {
    forDir("request-body")
  }

  test("test media-type") {
    forDir("media-type")
  }

  test("test encoding") {
    forDir("encoding")
  }

  test("test responses") {
    forDir("responses")
  }

  test("test link") {
    forDir("link")
  }

  test("test inner-security") {
    forDir("inner-security")
  }

  private def forDir(dir: String) = runTest(s"$dir/api.json", s"$dir/api-json-outline.json")
}
