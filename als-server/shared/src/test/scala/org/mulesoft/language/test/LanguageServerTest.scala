package org.mulesoft.language.test

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.high.level.amfmanager.AmfInitializationHandler
import org.mulesoft.language.common.dtoTypes.IOpenedDocument
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.server.core.Server
import org.mulesoft.language.server.modules.astManager.{ASTManager, ASTManagerModule}
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule
import org.mulesoft.language.server.modules.findDeclaration.FindDeclarationModule
import org.mulesoft.language.server.modules.findReferences.FindReferencesModule
import org.mulesoft.language.server.modules.hlastManager.HlAstManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.rename.RenameModule
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.modules.validationManager.ValidationManager
import org.mulesoft.language.test.clientConnection.TestClientConnection
import org.mulesoft.language.test.serverConnection.TestServerConnection
import org.scalatest.AsyncFunSuite

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

trait LanguageServerTest extends AsyncFunSuite with PlatformSecrets {

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def rootPath: String

  def format: String

  var clientOpt: Option[TestClientConnection] = None

  def init(): Future[Unit] = AmfInitializationHandler.init()

  def getClient: Future[TestClientConnection] = {

    if (clientOpt.nonEmpty) {
      Future.successful(clientOpt.get)
    } else {
      var serverList: ListBuffer[TestServerConnection] = ListBuffer()
      var clientList: ListBuffer[TestClientConnection] = ListBuffer()
      val thread = new Runnable {
        def run {
          var serverConnection = new TestServerConnection(clientList)
          serverList += serverConnection

          val server = new Server(serverConnection, TestPlatformDependentPart())

          server.registerModule(new ASTManager())
          server.registerModule(new HlAstManager())
          server.registerModule(new ValidationManager())
          server.registerModule(new SuggestionsManager())
          server.registerModule(new StructureManager())

          server.registerModule(new FindReferencesModule())
          server.registerModule(new FindDeclarationModule())
          server.registerModule(new RenameModule())

          server.enableModule(ASTManagerModule.moduleId)
          server.enableModule(HlAstManager.moduleId)
          server.enableModule(ValidationManager.moduleId)
          server.enableModule(SuggestionsManager.moduleId)
          server.enableModule(StructureManager.moduleId)

          server.enableModule(FindReferencesModule.moduleId)
          server.enableModule(FindDeclarationModule.moduleId)

          server.enableModule(RenameModule.moduleId)

          val editorManager = server.modules.get(EditorManagerModule.moduleId)
          if (editorManager.isDefined) {
            serverConnection.editorManager = Some(editorManager.get.asInstanceOf[EditorManagerModule])
          }

        }
      }
      thread.run
      clientList += new TestClientConnection(serverList)
      clientOpt = clientList.headOption
      Future.successful(clientList.head)
    }
  }

  var emptyClientOpt: Option[TestClientConnection] = None

  def getEmptyClient: Future[TestClientConnection] = {

    if (emptyClientOpt.nonEmpty) {
      return Future.successful(emptyClientOpt.get)
    }

    var serverList: ListBuffer[TestServerConnection] = ListBuffer()
    var clientList: ListBuffer[TestClientConnection] = ListBuffer()
    val thread = new Runnable {
      def run {
        var serverConnection = new TestServerConnection(clientList)
        serverList += serverConnection

        val server = new Server(serverConnection, TestPlatformDependentPart())

        val editorManager = server.modules.get(EditorManagerModule.moduleId)
        if (editorManager.isDefined) {
          serverConnection.editorManager = Some(editorManager.get.asInstanceOf[EditorManagerModule])
        }

      }
    }
    thread.run
    clientList += new TestClientConnection(serverList)
    emptyClientOpt = clientList.headOption
    Future.successful(clientList.head)
  }

  def getActualOutline(url: String, shortUrl: String): Future[DocumentSymbol] = {

    var position = 0

    var contentOpt: Option[String] = None
    this.platform
      .resolve(url)
      .flatMap(content => {

        val doc = IOpenedDocument(shortUrl, 0, content.stream.toString)
        getClient.flatMap(client => {
          client.documentOpened(doc)
          client
            .getStructure(shortUrl)
            .map(result => {
              client.documentClosed(shortUrl)
              result
            })
        })
      })
      .map(x => x.asInstanceOf[DocumentSymbol])
  }
  def filePath(path: String): String = {
    var rootDir = System.getProperty("user.dir")
    s"file://als-server/shared/src/test/resources/$rootPath/$path".replace('\\', '/').replace("null/", "")
  }
}
