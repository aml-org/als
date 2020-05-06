package org.mulesoft.language.outline.test.async20

import org.mulesoft.language.outline.test.BaseStructureTest

class YamlStructureTest extends BaseStructureTest {

  override def rootPath: String = "Async20"

  test("test root") {
    forDir("root")
  }

  test("test channel") {
    forDir("channel")
  }

  private def forDir(dir: String) = runTest(s"$dir/api.yml", s"$dir/api-yaml-outline.json")
}
