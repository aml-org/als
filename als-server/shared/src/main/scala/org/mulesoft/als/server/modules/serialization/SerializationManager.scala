package org.mulesoft.als.server.modules.serialization

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.server.modules.ast.BaseUnitListener
import org.mulesoft.als.server.modules.workspace.DiagnosticsBundle
import org.mulesoft.als.server.{ClientNotifierModule, SerializationProps}
import org.mulesoft.amfmanager.{AmfParseResult, ParserHelper}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.serialization.{SerializationClientCapabilities, SerializationMessage}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SerializationManager[S](platform: Platform, props: SerializationProps[S])
    extends ClientNotifierModule[SerializationClientCapabilities, Unit]
    with BaseUnitListener {

  private var enabled: Boolean = false

  override val `type`: SerializationConfigType.type = SerializationConfigType

  private def resolveAndSerialize(model: BaseUnit) = {
    val resolved = ParserHelper(platform).editingResolve(model)
    val value    = props.newDocBuilder()
    ParserHelper.toJsonLD(resolved, value).map(_ => value)
  }

  /**
    * Called on new AST available
    *
    * @param ast  - AST
    * @param uuid - telemetry UUID
    */
  override def onNewAst(ast: (AmfParseResult, Map[String, DiagnosticsBundle]), uuid: String): Unit = {
    if (enabled) {
      val cloned = ast._1.baseUnit.cloneUnit()
      resolveAndSerialize(cloned).map { b =>
        props.alsClientNotifier.notifySerialization(SerializationMessage(b.result))
      }
    }
  }

  override def onRemoveFile(uri: String): Unit = {}

  override def applyConfig(config: Option[SerializationClientCapabilities]): Unit = {
    config.foreach(c => enabled = c.acceptsNotification)
  }

  override def initialize(): Future[Unit] = Future.successful()
}

case object SerializationConfigType extends ConfigType[SerializationClientCapabilities, Unit]
