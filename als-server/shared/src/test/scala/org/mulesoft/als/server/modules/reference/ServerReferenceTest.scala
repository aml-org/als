package org.mulesoft.als.server.modules.reference

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.reference.{ReferenceContext, ReferenceParams, ReferenceRequestType}

abstract class ServerReferenceTest extends LanguageServerBaseTest {

  override def addModules(documentManager: TextDocumentManager,
                          platform: Platform,
                          directoryResolver: DirectoryResolver,
                          baseEnvironment: Environment,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val astManager       = new AstManager(documentManager, baseEnvironment, platform, logger)
    val hlAstManager     = new HlAstManager(documentManager, astManager, platform, logger)
    val referencesModule = new FindReferencesModule(hlAstManager, platform, logger)

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
      val ind      = content1.indexOf("MyType:") + 2
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
