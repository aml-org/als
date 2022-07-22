package org.mulesoft.als.server.modules.diagnostic

import amf.custom.validation.client.scala.BaseProfileValidatorBuilder
import org.mulesoft.als.server.MockDiagnosticClientNotifier
import org.mulesoft.als.server.protocol.LanguageServer

trait DiagnosticServer {

  def buildServer(
                   diagnosticNotifier: MockDiagnosticClientNotifier,
                   validator: Option[BaseProfileValidatorBuilder] = None
                 ): LanguageServer
}
