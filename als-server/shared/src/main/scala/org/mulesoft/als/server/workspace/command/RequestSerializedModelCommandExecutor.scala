package org.mulesoft.als.server.workspace.command

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.lsp.textsync.{SerializeModelRequestParams, ValidationRequestParams}
import org.yaml.model.YMap
import amf.core.parser._
import amf.core.remote.Platform
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfmanager.ParserHelper
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequestSerializedModelCommandExecutor(val logger: Logger, wsc: WorkspaceManager, platform: Platform)
    extends CommandExecutor[SerializeModelRequestParams, String] {
  override protected def buildParamFromMap(ast: YMap): Option[SerializeModelRequestParams] = {
    ast.key("uri").flatMap(e => e.value.toOption[String]).map(SerializeModelRequestParams)
  }

  override protected def runCommand(param: SerializeModelRequestParams): Future[String] = {
    val helper = ParserHelper(platform)
    helper.parse(param.uri, wsc.getWorkspace(param.uri).environment).map { model =>
      val resolved = helper.editingResolve(model.baseUnit)
      ParserHelper.toJsonLD(resolved)
    }
  }

}
