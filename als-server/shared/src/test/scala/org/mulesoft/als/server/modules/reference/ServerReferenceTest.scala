package org.mulesoft.als.server.modules.reference

import common.dtoTypes.Position
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.platform.ServerPlatform
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.reference.{ReferenceContext, ReferenceParams, ReferenceRequestType}

abstract class ServerReferenceTest extends LanguageServerBaseTest {

  override def addModules(documentManager: TextDocumentManager,
                          serverPlatform: ServerPlatform,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val astManager = new AstManager(documentManager, serverPlatform, logger)
    val hlAstManager = new HlAstManager(documentManager, astManager, serverPlatform, logger)
    val referencesModule = new FindReferencesModule(hlAstManager, serverPlatform, logger)

    builder
      .addInitializable(astManager)
      .addInitializable(hlAstManager)
      .addRequestModule(referencesModule)
  }

  test("Find references test 001") {
    withServer { server =>
      val content1 =
        """#%RAML 1.0
          |title: test
          |types:
          |  MyType:
          |  MyType2:
          |    properties:
          |      p1: MyType
          |""".stripMargin
      val ind = content1.indexOf("MyType:") + 2
      val position = LspConverter.toLspPosition(Position(ind, content1))

      val url = "file:///findReferencesTest001.raml"

      openFile(server)(url, content1)

      val handler = server.resolveHandler(ReferenceRequestType).value
      handler(ReferenceParams(TextDocumentIdentifier(url), position, ReferenceContext(false)))
        .map(references => {
          closeFile(server)(url)

          if (references.nonEmpty) {
            succeed
          } else {
            fail("No references have been found")
          }
        })
    }
  }
}
