package org.mulesoft.als.server.workspace.command

import amf.core.parser._
import amf.core.remote.Platform
import amf.core.validation.AMFValidationReport
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.textsync.ValidationRequestParams
import org.yaml.model.YMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequestAMFFullValidationCommandExecutor(val logger: Logger, wsc: WorkspaceManager, platform: Platform)
    extends CommandExecutor[ValidationRequestParams, AMFValidationReport] {
  override protected def buildParamFromMap(m: YMap): Option[ValidationRequestParams] = {
    m.key("mainUri").flatMap(e => e.value.toOption[String]).map(ValidationRequestParams)
  }

  override protected def runCommand(param: ValidationRequestParams): Future[AMFValidationReport] = {
    val helper = ParserHelper.apply(platform)
    helper
      .parse(param.mainUri, wsc.getWorkspace(param.mainUri).environment)
      .flatMap(pr => ParserHelper.report(pr.baseUnit))
  }
}
