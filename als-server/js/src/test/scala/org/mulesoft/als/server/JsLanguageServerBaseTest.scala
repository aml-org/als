package org.mulesoft.als.server

abstract class JsLanguageServerBaseTest extends LanguageServerBaseTest {
  override def filePath(path: String): String = {
    s"file://als-server/js/src/test/resources/$rootPath/$path"
      .replace('\\', '/')
      .replace("null/", "")
  }
}
