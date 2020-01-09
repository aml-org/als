package org.mulesoft.als.server

import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.PrintLnLogger
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.vscode._
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.lsp.server.DefaultServerSystemConf

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import org.mulesoft.als.client.convert.LspConvertersSharedToClient._
import org.mulesoft.als.client.lsp.feature.diagnostic.ClientPublishDiagnosticsParams
// $COVERAGE-OFF$ Incompatibility between scoverage and scalaJS

object JsPrintLnLogger {
  def apply(): Logger =
    js.Dynamic
      .literal(
        error = (message: String) => println(message),
        warn = (message: String) => println(message),
        info = (message: String) => println(message),
        log = (message: String) => println(message),
      )
      .asInstanceOf[Logger]
}

@JSExportAll
@JSExportTopLevel("AlsConnectionFactory")
class AlsConnectionFactory {
  def fromReaders(reader: MessageReader, writer: MessageWriter): AlsConnection = {
    val connection = ProtocolConnection(reader, writer, JsPrintLnLogger())

    val notifier = new ClientNotifier {
      override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {
        val clientParams: ClientPublishDiagnosticsParams = params.toClient
        connection.sendNotification[ClientPublishDiagnosticsParams, js.Any](PublishDiagnosticsNotification.`type`, clientParams)
      }

      override def notifyTelemetry(params: TelemetryMessage): Unit = {}
    }

    val managerFactory = ManagersFactory(notifier, PrintLnLogger)

    val server = new LanguageServerBuilder(managerFactory.documentManager, managerFactory.workspaceManager, DefaultServerSystemConf)
      .addInitializable(managerFactory.diagnosticManager)
      .addInitializable(managerFactory.workspaceManager)
      .addRequestModule(managerFactory.completionManager)
      .addRequestModule(managerFactory.structureManager)
      .build()

    new AlsConnection(connection, server)
  }

  // $COVERAGE-ON$
}
