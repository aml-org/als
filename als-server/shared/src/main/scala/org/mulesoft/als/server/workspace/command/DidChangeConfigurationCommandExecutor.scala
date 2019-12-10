package org.mulesoft.als.server.workspace.command

import amf.core.parser._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.CHANGE_CONFIG
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.textsync.DidChangeConfigurationNotificationParams
import org.yaml.model.{YMap, YSequence}

class DidChangeConfigurationCommandExecutor(val logger: Logger, wsc: WorkspaceManager)
    extends CommandExecutor[DidChangeConfigurationNotificationParams] {
  override protected def buildParamFromMap(m: YMap): Option[DidChangeConfigurationNotificationParams] = {
    val mainUri: String = m.key("mainUri").flatMap(e => e.value.toOption[String]).getOrElse("")
    m.key("dependencies").map { n =>
      n.value.value
    } match {
      case Some(dependencies: YSequence) =>
        Some(
          DidChangeConfigurationNotificationParams(mainUri,
                                                   dependencies.nodes.map(_.toOption[String].getOrElse("")).toSet))
      case _ => None
    }
  }

  override protected def runCommand(param: DidChangeConfigurationNotificationParams): Unit = {
    val manager = wsc.getWorkspace(param.mainUri)
    wsc.contentManagerConfiguration(manager, param.mainUri, param.dependencies, None)
    manager.changedFile(param.mainUri, CHANGE_CONFIG)
  }
}
