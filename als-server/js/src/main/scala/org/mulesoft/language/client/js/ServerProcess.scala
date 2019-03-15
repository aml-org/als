// $COVERAGE-OFF$
package org.mulesoft.language.client.js

import org.mulesoft.language.client.js.dtoTypes.{ProtocolMessagePayload, StructureReport}
import org.mulesoft.language.client.js.serverConnection.{NodeServerConnection, ProtocolMessage}
import org.mulesoft.language.entryPoints.common.ProtocolSeqMessage
import org.mulesoft.language.server.modules.astManager.{ASTManager, ASTManagerModule}
import org.mulesoft.language.server.modules.dialectManager.DialectManager
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule
import org.mulesoft.language.server.modules.findDeclaration.FindDeclarationModule
import org.mulesoft.language.server.modules.findReferences.FindReferencesModule
import org.mulesoft.language.server.modules.hlastManager.HlAstManager
import org.mulesoft.language.server.modules.outline.StructureManager
import org.mulesoft.language.server.modules.suggestions.SuggestionsManager
import org.mulesoft.language.server.modules.validationManager.ValidationManager

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import org.mulesoft.language.client.js.CustomPicklerConfig.{macroRW, ReadWriter => RW}
import org.mulesoft.language.server.core.Server

import scala.language.postfixOps

@js.native
@JSImport("aml-shacl-node", JSImport.Default)
object SHACLValidator extends js.Any {}

@js.native
@JSImport("ajv", JSImport.Default)
object AjvModule extends js.Object {}

@js.native
trait Process extends js.Object {

  /**
    * Binds to process events.
    *
    * @param eventType
    * @param callBack
    */
  def on(eventType: String, callBack: js.Function1[js.Any, Unit]): Unit = js.native

  def send(message: js.Any): Unit = js.native

  var platform: String = js.native
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
      connection.handleJSONMessageReceived(data)
    })

    val server = new Server(connection, JSPlatformDependentPart)

    server.registerModule(new ASTManager())
    server.registerModule(new DialectManager())
    server.registerModule(new HlAstManager())
    server.registerModule(new ValidationManager())
    server.registerModule(new SuggestionsManager())
    server.registerModule(new StructureManager())

    server.registerModule(new FindReferencesModule())
    server.registerModule(new FindDeclarationModule())

    server.enableModule(ASTManagerModule.moduleId)
    //server.enableModule(IDialectManagerModule.moduleId)
    server.enableModule(HlAstManager.moduleId)
    server.enableModule(ValidationManager.moduleId)
    server.enableModule(SuggestionsManager.moduleId)
    server.enableModule(StructureManager.moduleId)

    server.enableModule(FindReferencesModule.moduleId)
    server.enableModule(FindDeclarationModule.moduleId)

    val editorManager = server.modules.get(EditorManagerModule.moduleId)
    if (editorManager.isDefined) {
      connection.editorManager = Some(editorManager.get.asInstanceOf[EditorManagerModule])
    }
  }
}

// $COVERAGE-ON$
