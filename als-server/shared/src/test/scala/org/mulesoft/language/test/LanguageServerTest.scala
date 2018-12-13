package org.mulesoft.language.test

import amf.core.AMF
import amf.core.unsafe.PlatformSecrets
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.webapi.{Oas20Plugin, Oas30Plugin, Raml08Plugin, Raml10Plugin}
import amf.plugins.document.webapi.validation.PayloadValidatorPlugin
import amf.plugins.features.validation.AMFValidatorPlugin
import org.mulesoft.language.server.core.Server
import org.mulesoft.language.server.modules.findDeclaration.FIndDeclarationModule
import org.mulesoft.language.server.modules.findReferences.{FindReferencesModule, RenameModule}
import org.mulesoft.language.server.modules.hlastManager.HLASTManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.server.modules.astManager.{ASTManager, IASTManagerModule}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule
import org.mulesoft.language.server.server.modules.validationManager.ValidationManager
import org.mulesoft.language.test.clientConnection.TestClientConnetcion
import org.mulesoft.language.test.dtoTypes.{GetCompletionRequest, OpenedDocument}
import org.mulesoft.language.test.serverConnection.TestServerConnection
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

abstract class LanguageServerTest extends AsyncFunSuite with PlatformSecrets{


    implicit override def executionContext:ExecutionContext =
        scala.concurrent.ExecutionContext.Implicits.global

    def rootPath:String

    def format:String

    var clientOpt:Option[TestClientConnetcion] = None

    def init():Future[Unit] = {
        amf.core.AMF.registerPlugin(AMLPlugin)
        amf.core.AMF.registerPlugin(Raml10Plugin)
        amf.core.AMF.registerPlugin(Raml08Plugin)
        amf.core.AMF.registerPlugin(Oas20Plugin)
        amf.core.AMF.registerPlugin(Oas30Plugin)
        amf.core.AMF.registerPlugin(AMFValidatorPlugin)
        amf.core.AMF.registerPlugin(PayloadValidatorPlugin)
        AMF.init()
    } recoverWith {
        case e: Throwable => Future.successful()
        case _ => Future.successful()
    }

    def getClient: Future[TestClientConnetcion] = {

        if(clientOpt.nonEmpty){
            return Future.successful(clientOpt.get)
        }

        var serverList:ListBuffer[TestServerConnection] = ListBuffer()
        var clientList:ListBuffer[TestClientConnetcion] = ListBuffer()
        var thread = new Runnable {
            def run {
                var serverConnection = new TestServerConnection(clientList)
                serverList += serverConnection


                    val server = new Server(serverConnection, TestPlatformDependentPart())

                    server.registerModule(new ASTManager())
                    server.registerModule(new HLASTManager())
                    server.registerModule(new ValidationManager())
                    server.registerModule(new SuggestionsManager())
                    server.registerModule(new StructureManager())

                    server.registerModule(new FindReferencesModule())
                    server.registerModule(new FIndDeclarationModule())
                    server.registerModule(new RenameModule())

                    server.enableModule(IASTManagerModule.moduleId)
                    server.enableModule(HLASTManager.moduleId)
                    server.enableModule(ValidationManager.moduleId)
                    server.enableModule(SuggestionsManager.moduleId)
                    server.enableModule(StructureManager.moduleId)

                    server.enableModule(FindReferencesModule.moduleId)
                    server.enableModule(FIndDeclarationModule.moduleId)

                    server.enableModule(RenameModule.moduleId)

                    val editorManager = server.modules.get(IEditorManagerModule.moduleId)
                    if (editorManager.isDefined) {
                        serverConnection.editorManager = Some(editorManager.get.asInstanceOf[IEditorManagerModule])
                    }

            }
        }
        thread.run
        clientList += new TestClientConnetcion(serverList)
        clientOpt = clientList.headOption
        Future.successful(clientList.head)
    }

    var emptyClientOpt:Option[TestClientConnetcion] = None

    def getEmptyClient: Future[TestClientConnetcion] = {

        if(emptyClientOpt.nonEmpty){
            return Future.successful(emptyClientOpt.get)
        }

        var serverList:ListBuffer[TestServerConnection] = ListBuffer()
        var clientList:ListBuffer[TestClientConnetcion] = ListBuffer()
        var thread = new Runnable {
            def run {
                var serverConnection = new TestServerConnection(clientList)
                serverList += serverConnection

                val server = new Server(serverConnection, TestPlatformDependentPart())

                val editorManager = server.modules.get(IEditorManagerModule.moduleId)
                if (editorManager.isDefined) {
                    serverConnection.editorManager = Some(editorManager.get.asInstanceOf[IEditorManagerModule])
                }

            }
        }
        thread.run
        clientList += new TestClientConnetcion(serverList)
        emptyClientOpt = clientList.headOption
        Future.successful(clientList.head)
    }

    def filePath(path:String):String = {
        s"file://als-server/shared/src/test/resources/$rootPath/$path".replace('\\','/').replace("null/", "")
    }

}
