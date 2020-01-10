package org.mulesoft.als.server.workspace.command

import amf.core.parser._
import amf.core.remote.Platform
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.diagnostic.DiagnosticConverters
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.textsync.ValidationRequestParams
import org.yaml.model.YMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequestAMFFullValidationCommandExecutor(val logger: Logger, wsc: WorkspaceManager, platform: Platform)
    extends CommandExecutor[ValidationRequestParams, Seq[PublishDiagnosticsParams]] {
  override protected def buildParamFromMap(m: YMap): Option[ValidationRequestParams] = {
    m.key("mainUri").flatMap(e => e.value.toOption[String]).map(ValidationRequestParams)
  }

  override protected def runCommand(param: ValidationRequestParams): Future[Seq[PublishDiagnosticsParams]] = {
    logger.debug(s"about to parse: ${param.mainUri}", "RequestAMFFullValidationCommandExecutor", "runCommand")

    val helper = ParserHelper.apply(platform)
    helper
      .parse(param.mainUri, wsc.getWorkspace(param.mainUri).environment)
      .flatMap(pr => {
        logger.debug(s"about to report: ${param.mainUri}", "RequestAMFFullValidationCommandExecutor", "runCommand")
        ParserHelper.report(pr.baseUnit).map(r => (r, pr.groupedErrors))
      })
      .map { t =>
        val report = t._1
        val ge     = t._2
        logger.debug(s"report conforms: ${report.conforms}", "RequestAMFFullValidationCommandExecutor", "runCommand")
        DiagnosticConverters
          .buildIssueResults(report.results.groupBy(r => r.location.getOrElse(param.mainUri)) ++ ge, Map.empty)
          .map(_.publishDiagnosticsParams)
      }
  }
}
