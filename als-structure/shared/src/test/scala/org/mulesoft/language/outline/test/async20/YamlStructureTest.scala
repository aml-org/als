package org.mulesoft.language.outline.test.async20

import org.mulesoft.language.outline.test.BaseStructureTest

class YamlStructureTest extends BaseStructureTest {

  override def rootPath: String = "Async20"

  test("test root") {
    forDir("root")
  }

  test("test payload") {
    forDir("payload")
  }

  test("test servers") {
    forDir("servers")
  }

  test("test channel") {
    forDir("channel")
  }

  test("test schemas") {
    forDir("schemas")
  }

  test("test declarations/channel-bindings") {
    forDir("declarations/channel-bindings")
  }

  test("test declarations/correlations-ids") {
    forDir("declarations/correlations-ids")
  }

  test("test declarations/message-bindings") {
    forDir("declarations/message-bindings")
  }

  test("test declarations/message-traits") {
    forDir("declarations/message-traits")
  }

  test("test declarations/messages") {
    forDir("declarations/messages")
  }

  test("test declarations/operation-bindings") {
    forDir("declarations/operation-bindings")
  }

  test("test declarations/operation-traits") {
    forDir("declarations/operation-traits")
  }

  test("test declarations/parameters") {
    forDir("declarations/parameters")
  }

  test("test declarations/schemas") {
    forDir("declarations/schemas")
  }

  test("test declarations/security-schemes") {
    forDir("declarations/security-schemes")
  }

  test("test declarations/server-bindings") {
    forDir("declarations/server-bindings")
  }

  private def forDir(dir: String) = runTest(s"$dir/api.yml", s"$dir/api-yaml-outline.json")
}
