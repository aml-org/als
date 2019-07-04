package org.mulesoft.als.server.modules.quickfixes

import org.mulesoft.als.server.RequestModule
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.codeactions._

import scala.concurrent.Future

class QuickFixesModule extends RequestModule[CodeActionCapabilities, CodeActionOptions] {
  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[CodeActionParams, Seq[CodeAction]] {
      override def `type`: CodeActionRequestType.type = CodeActionRequestType

      override def apply(params: CodeActionParams): Future[Seq[CodeAction]] =
        Future.successful(Seq())
    }
  )

  override val `type`: CodeActionConfigType.type = CodeActionConfigType

  override def applyConfig(config: Option[CodeActionCapabilities]): CodeActionOptions =
    CodeActionOptions(Some(Seq(CodeActionKind.QuickFix.toString)))

  override def initialize(): Future[Unit] = Future.successful()
}
