package org.mulesoft.als.server.modules.quickfixes

import org.mulesoft.als.server.RequestModule
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.codeactions._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.Future

class QuickFixesModule(telemetryProvider: TelemetryProvider)
    extends RequestModule[CodeActionCapabilities, CodeActionOptions] {
  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[CodeActionParams, Seq[CodeAction]] {
      override def `type`: CodeActionRequestType.type = CodeActionRequestType

      override def task(params: CodeActionParams): Future[Seq[CodeAction]] =
        Future.successful(Seq())

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: CodeActionParams): String = "QuickFixesModule"

      override protected def beginType(params: CodeActionParams): MessageTypes = MessageTypes.BEGIN_QUICK_FIX

      override protected def endType(params: CodeActionParams): MessageTypes = MessageTypes.END_QUICK_FIX

      override protected def msg(params: CodeActionParams): String =
        s"Requested quick fixes for ${params.textDocument.uri}"

      override protected def uri(params: CodeActionParams): String = params.textDocument.uri
    }
  )

  override val `type`: CodeActionConfigType.type = CodeActionConfigType

  override def applyConfig(config: Option[CodeActionCapabilities]): CodeActionOptions =
    CodeActionOptions(Some(Seq(CodeActionKind.QuickFix.toString)))

  override def initialize(): Future[Unit] = Future.successful()
}
