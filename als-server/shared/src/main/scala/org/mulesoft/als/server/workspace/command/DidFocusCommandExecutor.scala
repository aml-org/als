package org.mulesoft.als.server.workspace.command

import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.FOCUS_FILE
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.yaml.model.YMap
import amf.core.parser._
import org.mulesoft.als.server.protocol.textsync.DidFocusParams

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DidFocusCommandExecutor(val logger: Logger, wsc: WorkspaceManager)
    extends CommandExecutor[DidFocusParams, Unit] {
  override protected def buildParamFromMap(m: YMap): Option[DidFocusParams] = {
    val version: Int = m.key("version").flatMap(e => e.value.toOption[Int]).getOrElse(1)
    m.key("uri").map { n =>
      n.value.asScalar.map(_.text).getOrElse(n.value.toString)
    } match {
      case Some(uri) => Some(DidFocusParams(uri, version))
      case _         => None
    }
  }

  override protected def runCommand(param: DidFocusParams): Future[Unit] =
    wsc.getWorkspace(param.uri).map(_.stage(param.uri, FOCUS_FILE))
}
