package org.mulesoft.language.test

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.language.common.dtoTypes.IOpenedDocument
import org.mulesoft.language.server.core.Server
import org.mulesoft.language.server.modules.findDeclaration.FIndDeclarationModule
import org.mulesoft.language.server.modules.findReferences.FindReferencesModule
import org.mulesoft.language.server.modules.hlastManager.HLASTManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.server.modules.astManager.{ASTManager, IASTManagerModule}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule
import org.mulesoft.language.server.server.modules.validationManager.ValidationManager
import org.mulesoft.language.test.clientConnection.TestClientConnetcion
import org.mulesoft.language.test.dtoTypes.{GetCompletionRequest, OpenedDocument}
import org.mulesoft.language.test.serverConnection.TestServerConnection
import org.scalatest.AsyncFunSuite

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

abstract class LanguageServerTest extends AsyncFunSuite with PlatformSecrets{



    def rootPath:String

    var clientOpt:Option[TestClientConnetcion] = None

    def getClient: Future[TestClientConnetcion] = {

        if(clientOpt.nonEmpty){
            return Future.successful(clientOpt.get)
        }

        var serverOpt:Option[TestServerConnection] = None
        var clientList:ListBuffer[TestClientConnetcion] = ListBuffer()
        var thread = new Runnable {
            def run {
                var serverConnection = new TestServerConnection(clientList)
                serverOpt = Some(serverConnection)

                val server = new Server(serverConnection, TestPlatformDependentPart())

                server.registerModule(new ASTManager())
                server.registerModule(new HLASTManager())
                server.registerModule(new ValidationManager())
                server.registerModule(new SuggestionsManager())
                server.registerModule(new StructureManager())

                server.registerModule(new FindReferencesModule())
                server.registerModule(new FIndDeclarationModule())

                server.enableModule(IASTManagerModule.moduleId)
                server.enableModule(HLASTManager.moduleId)
                server.enableModule(ValidationManager.moduleId)
                server.enableModule(SuggestionsManager.moduleId)
                server.enableModule(StructureManager.moduleId)

                server.enableModule(FindReferencesModule.moduleId)
                server.enableModule(FIndDeclarationModule.moduleId)

                val editorManager = server.modules.get(IEditorManagerModule.moduleId)
                if (editorManager.isDefined) {
                    serverConnection.editorManager = Some(editorManager.get.asInstanceOf[IEditorManagerModule])
                }
            }
        }
        thread.run

        clientList += new TestClientConnetcion(serverOpt)
        Future.successful(clientList.head).flatMap(client=>{
            val content = "#%RAML 1.0\n title: init\n\n"
            val url = "/init.raml"
            client.documentOpened(IOpenedDocument(url,0,content))
            client.getSuggestions(url,content.length-1).map(x=>{
                client.documentClosed(url)
                clientOpt = clientList.headOption
                client
            })
        })
    }

    def filePath(path:String):String = {
        var rootDir = System.getProperty("user.dir")
        s"file://$rootDir/shared/src/test/resources/$rootPath/$path".replace('\\','/').replace("null/", "")
    }

}
