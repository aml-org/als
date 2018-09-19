// $COVERAGE-OFF$
package org.mulesoft.language.client.js

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobalScope, JSImport}
import CustomPicklerConfig._
import org.mulesoft.language.client.js.dtoTypes.{ProtocolMessagePayload, StructureReport}
import org.mulesoft.language.client.js.serverConnection.{NodeServerConnection, ProtocolMessage}
import org.mulesoft.language.common.logger.PrintlnLogger
import org.mulesoft.language.entryPoints.common.ProtocolSeqMessage
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.modules.findDeclaration.FIndDeclarationModule
import org.mulesoft.language.server.modules.findReferences.FindReferencesModule
import org.mulesoft.language.server.modules.hlastManager.HLASTManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.server.modules.astManager.{ASTManager, IASTManagerModule, ParseResult, ParserHelper}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.{EditorManager, IEditorManagerModule, TextBufferInfo}
import org.mulesoft.language.server.server.modules.validationManager.ValidationManager
import upickle.json

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
//import upickle.default._
import amf.core.client.{ExitCodes, ParserConfig}
import amf.core.model.document.{BaseUnit, Document}
import amf.core.unsafe.TrunkPlatform
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import amf.plugins.domain.webapi.models.WebApi

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import CustomPicklerConfig.{ReadWriter => RW, macroRW}
import org.mulesoft.language.server.core.Server

@js.native
@JSImport("aml-shacl-node", JSImport.Default)
object SHACLValidator extends js.Any {

}

@js.native
@JSImport("ajv", JSImport.Default)
object AjvModule extends js.Object {}

@js.native
trait Process extends js.Object {

  /**
    * Binds to process events.
    * @param eventType
    * @param callBack
    */
  def on(eventType: String, callBack: js.Function1[js.Any, Unit]) : Unit = js.native;

  def send(message: js.Any) : Unit = js.native;
  
  var platform: String = js.native;
}

@js.native
trait JSON extends js.Object {
  def parse(text: String): js.Any = js.native

  def stringify(value: js.Any): String = js.native
}

object ServerProcess {

  var lastStructureReport: Option[StructureReport] = None

  implicit def rw: RW[ProtocolMessage[ProtocolMessagePayload]] = macroRW
  implicit def rwSeq: RW[ProtocolSeqMessage[ProtocolMessagePayload]] = macroRW

  def main(args: Array[String]): Unit = {
    Globals.SHACLValidator = SHACLValidator
    Globals.Ajv = AjvModule

    val connection = new NodeServerConnection()

    Globals.process.on("message", (data: js.Any) => {
      connection.handleJSONMessageRecieved(data)
    })

    val server = new Server(connection, JSPlatformDependentPart)

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
      connection.editorManager = Some(editorManager.get.asInstanceOf[IEditorManagerModule])
    }
  }
}

// $COVERAGE-ON$