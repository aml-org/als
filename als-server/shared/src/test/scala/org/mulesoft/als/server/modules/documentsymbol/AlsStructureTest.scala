package org.mulesoft.als.server.modules.documentsymbol

import org.mulesoft.language.outline.test.StructureTest

abstract class AlsStructureTest extends StructureTest {

  override def filePath(path: String): String = {
    var rootDir = System.getProperty("user.dir")
    s"file://als-server/shared/src/test/resources/$rootPath/$path".replace('\\', '/').replace("null/", "")
  }
}
