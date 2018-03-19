package org.mulesoft.language.client.js

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobalScope, JSImport}
import CustomPicklerConfig._
import org.mulesoft.language.common.logger.PrintlnLogger
import org.mulesoft.language.server.common.utils.TypeName
import org.mulesoft.language.server.server.modules.astManager.{ASTManager, ParseResult, ParserHelper}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.{EditorManager, TextBufferInfo}
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
@JSImport("@mulesoft/amf-shacl-node", JSImport.Default)
object SHACLValidator extends js.Any {

}

@js.native
trait Process extends js.Object {

  /**
    * Binds to process events.
    * @param eventType
    * @param callBack
    */
  def on(eventType: String, callBack: js.Function1[js.Any, Unit]) : Unit = js.native

  def send(message: js.Any) : Unit = js.native
}

@js.native
trait JSON extends js.Object {
  def parse(text: String): js.Any = js.native

  def stringify(value: js.Any): String = js.native
}

object Main {

  var lastStructureReport: Option[StructureReport] = None

  implicit def rw: RW[ProtocolMessage[ProtocolMessagePayload]] = macroRW

  def main(args: Array[String]): Unit = {
    Globals.SHACLValidator = SHACLValidator;

    val connection = new NodeServerConnection()

    Globals.process.on("message", (data: js.Any) => {
      connection.handleJSONMessageRecieved(data)
    })

    val server = new Server(connection)
    server.registerModule(new EditorManager())
    server.registerModule(new ASTManager())
    server.registerModule(new ValidationManager())
  }
}

