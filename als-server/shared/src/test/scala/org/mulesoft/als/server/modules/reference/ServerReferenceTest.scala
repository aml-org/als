package org.mulesoft.als.server.modules.reference

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.reference.{ReferenceContext, ReferenceParams, ReferenceRequestType}
import org.mulesoft.lsp.server.LanguageServer

class ServerReferenceTest extends LanguageServerBaseTest {

  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, platform, logger, withDiagnostics = false)

    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, platform)
      .build()
  }

  ignore("Find references test 001") {
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
      val ind      = content1.indexOf("MyType:") + 2
      val position = LspRangeConverter.toLspPosition(Position(ind, content1))

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

  override def rootPath: String = "actions/references"
}
