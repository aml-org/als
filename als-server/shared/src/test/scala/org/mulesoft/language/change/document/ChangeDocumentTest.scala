package org.mulesoft.language.change.document

import common.dtoTypes.Position
import org.mulesoft.language.common.dtoTypes.{ChangedDocument, OpenedDocument}
import org.mulesoft.language.server.core.Server
import org.mulesoft.language.server.modules.astManager.{ASTManager, ASTManagerModule}
import org.mulesoft.language.server.modules.commonInterfaces.{IPoint, IRange}
import org.mulesoft.language.server.modules.editorManager.{EditorManager, EditorManagerModule}
import org.mulesoft.language.server.modules.findDeclaration.FindDeclarationModule
import org.mulesoft.language.server.modules.findReferences.FindReferencesModule
import org.mulesoft.language.server.modules.hlastManager.HlAstManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.modules.validationManager.ValidationManager
import org.mulesoft.language.test.clientConnection.TestClientConnection
import org.mulesoft.language.test.serverConnection.TestServerConnection
import org.mulesoft.language.test.{LanguageServerTest, TestPlatformDependentPart}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

class ChangeDocumentTest extends LanguageServerTest {

  override def rootPath: String = ""

  override def format = "RAML 1.0"

  test("change document test 001") {
    val content1 = "#%RAML 1.0\ntitle: test\n"
    val content2 = "#%RAML 1.0\ntitle: test\ntypes:\n  MyType: number\n"

    val url = "file:///changeDocumentTest001.raml"
    for {
      _      <- init()
      _      <- org.mulesoft.high.level.Core.init()
      client <- getClient
      actualOutline <- {
        client.documentChanged(ChangedDocument(url, 0, Some(content1), None))
        client.documentChanged(ChangedDocument(url, 0, Some(content2), None))
        client.documentClosed(url)
        client.getStructure(url)
      }
    } yield {
      actualOutline
        .collectFirst({ case o if o.name == "MyType" => succeed })
        .getOrElse(fail("Invalid outline"))
    }
  }

  test("change document test 002") {
    var content1 = "#%RAML 1.0\ntitle: test\n"
    var content2 = "#%RAML 1.0\ntitle: test\n"
    var content3 = "#%RAML 1.0\ntitle: test\nsome invalid string\ntypes:\n  MyType: number\n"

    var url = "file:///changeDocumentTest002.raml"
    for {
      _      <- init()
      _      <- org.mulesoft.high.level.Core.init()
      client <- getClient
      actualOutline <- {
        client.documentChanged(ChangedDocument(url, 1, Some(content1), None))
        client.documentChanged(ChangedDocument(url, 0, Some(content1), None))
        client.documentChanged(ChangedDocument(url, 2, Some(content2), None))
        client.documentClosed(url)
        client.getStructure(url)
      }
    } yield {
      actualOutline
        .collectFirst({ case o if o.name == "MyType" => fail("Should fail") })
        .getOrElse(succeed)
    }
  }

  test("Find references test 001") {
    init().flatMap(_ => {
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

      val url = "file:///findReferencesTest001.raml"
      getClient.flatMap(client => {
        client.documentOpened(OpenedDocument(url, 0, content1))
        client
          .findReferences(url, Position(ind, content1))
          .map(refs => {
            client.documentClosed(url)
            if (refs.nonEmpty) {
              succeed
            } else {
              fail("No references have been found")
            }
          })
      })
    })
  }

  test("Open declaration test 001") {
    init().flatMap(_ => {
      var content1 =
        """#%RAML 1.0
          |title: test
          |types:
          |  MyType:
          |  MyType2:
          |    properties:
          |      p1: MyType
          |""".stripMargin
      var ind = content1.indexOf("p1: MyType") + "p1: My".length

      var url = "file:///findDeclarationTest001.raml"
      getClient.flatMap(client => {
        client.documentOpened(OpenedDocument(url, 0, content1))
        client
          .openDeclaration(url, Position(ind, content1))
          .map(refs => {
            client.documentClosed(url)
            if (refs.nonEmpty) {
              succeed
            } else {
              fail("No declaration has been found")
            }
          })
      })
    })
  }

  test("enable disable module test 001") {
    init().flatMap(_ => {
      var serverOpt: Option[TestServerConnection]      = None
      var clientList: ListBuffer[TestClientConnection] = ListBuffer()

      var serverConnection = new TestServerConnection(clientList)
      serverOpt = Some(serverConnection)

      val server = new Server(serverConnection, TestPlatformDependentPart())

      server.registerModule(new ASTManager())
      server.registerModule(new HlAstManager())
      server.registerModule(new ValidationManager())
      server.registerModule(new SuggestionsManager())
      server.registerModule(new StructureManager())

      server.registerModule(new FindReferencesModule())
      server.registerModule(new FindDeclarationModule())

      server.enableModule(ASTManagerModule.moduleId)
      server.enableModule(HlAstManager.moduleId)
      server.enableModule(ValidationManager.moduleId)
      server.enableModule(SuggestionsManager.moduleId)
      server.enableModule(StructureManager.moduleId)

      server.enableModule(FindReferencesModule.moduleId)
      server.enableModule(FindDeclarationModule.moduleId)

      server.disableModule(ASTManagerModule.moduleId)
      server.disableModule(HlAstManager.moduleId)
      server.disableModule(ValidationManager.moduleId)
      server.disableModule(SuggestionsManager.moduleId)
      server.disableModule(StructureManager.moduleId)

      server.disableModule(FindReferencesModule.moduleId)
      server.disableModule(FindDeclarationModule.moduleId)

      server.enableModule(ASTManagerModule.moduleId)
      server.enableModule(HlAstManager.moduleId)
      server.enableModule(ValidationManager.moduleId)
      server.enableModule(SuggestionsManager.moduleId)
      server.enableModule(StructureManager.moduleId)

      server.enableModule(FindReferencesModule.moduleId)
      server.enableModule(FindDeclarationModule.moduleId)

      val editorManager = server.modules.get(EditorManagerModule.moduleId)
      if (editorManager.isDefined) {
        serverConnection.editorManager = Some(editorManager.get.asInstanceOf[EditorManagerModule])

        val url = "file:///enable-disable.raml"
        serverConnection.editorManager.get
          .asInstanceOf[EditorManager]
          .onOpenDocument(
            OpenedDocument(url, 0, "#%RAML 1.0\r\ntitle:test api\r\nresuorceTypes:\r\n rt: !include /rt.raml"))
        serverConnection.editorManager.get
          .asInstanceOf[EditorManager]
          .onOpenDocument(OpenedDocument("file:///rt.raml", 0, "get:"))

        serverConnection.editorManager.get
          .getEditor(url)
          .foreach(ed => {
            var buf = ed.buffer
            buf.characterIndexForPosition(IPoint(0, 0))
            buf.rangeForRow(0, true)
            buf.getTextInRange(new IRange {
              var start = IPoint(0, 0)
              var end   = IPoint(1, 1)
            })
            buf.setTextInRange(new IRange {
              var start = IPoint(0, 0)
              var end   = IPoint(0, "#%RAML 1.0".length)
            }, "#%RAML 1.0 Overlay")
            buf.getEndPosition()
          })
      }
      Future.successful(succeed)
    })
  }

  test("try using uninitialized server 001") {
    val content1 = "#%RAML 1.0\ntitle: test\n"

    val url = "file:///changeDocumentTest001.raml"

    for {
      _      <- init()
      _      <- org.mulesoft.high.level.Core.init()
      c      <- getClient
      _      <- Future.successful(c.documentChanged(ChangedDocument(url, 0, Some(content1), None)))
      client <- getEmptyClient
      result <- client
        .getStructure(url)
        .map(actualOutline => {
          true
        }) recoverWith {
        case e: Throwable => Future.successful(false)
        case _            => Future.successful(false)
      }
    } yield {
      if (result) {
        fail("Should fail")
      } else {
        succeed
      }
    }
  }
}
