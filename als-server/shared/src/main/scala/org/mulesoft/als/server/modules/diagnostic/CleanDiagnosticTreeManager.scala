package org.mulesoft.als.server.modules.diagnostic

import amf.core.validation.AMFValidationResult
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.diagnostic._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.textsync.EnvironmentProvider
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CleanDiagnosticTreeManager(environmentProvider: EnvironmentProvider, logger: Logger)
    extends RequestModule[CleanDiagnosticTreeClientCapabilities, CleanDiagnosticTreeOptions] {

  private var enabled: Boolean = true

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[CleanDiagnosticTreeParams, Seq[PublishDiagnosticsParams]] {
      override def `type`: CleanDiagnosticTreeRequestType.type = CleanDiagnosticTreeRequestType

      override def apply(params: CleanDiagnosticTreeParams): Future[Seq[PublishDiagnosticsParams]] =
        validate(params.textDocument.uri)
    }
  )

  override val `type`: ConfigType[CleanDiagnosticTreeClientCapabilities, CleanDiagnosticTreeOptions] =
    CleanDiagnosticTreeConfigType

  override def applyConfig(config: Option[CleanDiagnosticTreeClientCapabilities]): CleanDiagnosticTreeOptions = {
    config.foreach(c => enabled = c.enableCleanDiagnostic)
    CleanDiagnosticTreeOptions(true)
  }

  override def initialize(): Future[Unit] = Future.successful()

  def validate(uri: String): Future[Seq[PublishDiagnosticsParams]] = {
    val helper = environmentProvider.amfConfiguration.parserHelper
    helper
      .parse(uri, environmentProvider.environmentSnapshot())
      .flatMap(pr => {
        logger.debug(s"about to report: $uri", "RequestAMFFullValidationCommandExecutor", "runCommand")
        ParserHelper.report(pr.baseUnit).map(r => (r, pr))
      })
      .map { t =>
        val list                                       = t._2.tree
        val ge: Map[String, List[AMFValidationResult]] = t._2.groupedErrors
        val report                                     = t._1
        val grouped                                    = report.results.groupBy(r => r.location.getOrElse(uri))

        val merged = list.map(uri => uri -> (ge.getOrElse(uri, Nil) ++ grouped.getOrElse(uri, Nil))).toMap
        logger.debug(s"report conforms: ${report.conforms}", "RequestAMFFullValidationCommandExecutor", "runCommand")

        DiagnosticConverters.buildIssueResults(merged, Map.empty).map(_.publishDiagnosticsParams)
      }
  }
}
