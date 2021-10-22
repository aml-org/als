package org.mulesoft.als.server.client

import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.protocol.client.{AlsLanguageClient, AlsLanguageClientAware}
import org.mulesoft.lsp.client.{LspLanguageClient, LspLanguageClientAware}
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams

case class ClientConnection[S](logger: Logger)
    extends LspLanguageClientAware
    with AlsLanguageClientAware[S]
    with ClientNotifier
    with AlsClientNotifier[S] {
  var clientProxy: Option[LspLanguageClient]       = None
  var alsClientProxy: Option[AlsLanguageClient[S]] = None

  override def connect(client: LspLanguageClient): Unit = clientProxy = Some(client)

  override def connectAls(client: AlsLanguageClient[S]): Unit = alsClientProxy = Some(client)

  override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = applyToClient { client =>
    client.publishDiagnostic(params)
  }

  override def notifyTelemetry(params: TelemetryMessage): Unit = applyToClient { client =>
    client.notifyTelemetry(params)
  }

  private def applyToClient(fn: LspLanguageClient => Unit): Unit =
    if (clientProxy.isEmpty)
      logger.warning("Client not connect", "ClientConnection", "applyToClient")
    else
      fn(clientProxy.get)

  private def applyToAlsClient(fn: AlsLanguageClient[S] => Unit): Unit =
    if (alsClientProxy.isEmpty)
      logger.warning("Als Client not connect", "ClientConnection", "applyToAlsClient")
    else
      fn(alsClientProxy.get)

  override def notifySerialization(params: SerializationResult[S]): Unit = applyToAlsClient { client =>
    client.notifySerialization(params)
  }

  override def notifyProjectFiles(params: FilesInProjectParams): Unit = applyToAlsClient { client =>
    client.notifyProjectFiles(params)
  }
}
