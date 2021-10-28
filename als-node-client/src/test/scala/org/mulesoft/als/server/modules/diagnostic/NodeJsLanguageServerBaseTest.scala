package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.JsLanguageServerBaseTest

abstract class NodeJsLanguageServerBaseTest extends JsLanguageServerBaseTest {
  override def filePath(path: String): String = {
    s"file://als-node-client/src/test/resources/$rootPath/$path"
      .replace('\\', '/')
      .replace("null/", "")
  }
}
