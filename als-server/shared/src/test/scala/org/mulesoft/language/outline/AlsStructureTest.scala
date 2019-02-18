package org.mulesoft.language.outline

import org.mulesoft.language.common.dtoTypes.OpenedDocument
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.test.StructureTest
import org.mulesoft.language.test.LanguageServerTest

import scala.concurrent.Future

abstract class AlsStructureTest extends StructureTest with LanguageServerTest {

  override def filePath(path: String): String = {
    var rootDir = System.getProperty("user.dir")
    s"file://als-server/shared/src/test/resources/$rootPath/$path".replace('\\', '/').replace("null/", "")
  }
}
