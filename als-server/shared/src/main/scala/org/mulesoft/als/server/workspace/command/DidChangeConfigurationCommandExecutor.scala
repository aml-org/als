package org.mulesoft.als.server.workspace.command

import amf.core.internal.parser._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.textsync.DidChangeConfigurationNotificationParams
import org.yaml.model.{YMap, YMapEntry, YSequence}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DidChangeConfigurationCommandExecutor(val logger: Logger, wsc: WorkspaceManager)
    extends CommandExecutor[DidChangeConfigurationNotificationParams, Unit] {
  override protected def buildParamFromMap(m: YMap): Option[DidChangeConfigurationNotificationParams] = {
    val mainUri: String                 = m.key("mainUri").flatMap(e => e.value.toOption[String]).getOrElse("")
    val folder: Option[String]          = m.key("folder").flatMap(e => e.value.toOption[String])
    val dependencies: Set[String]       = m.key("dependencies").map(seqToSet).getOrElse(Set.empty)
    val profiles: Set[String]           = m.key("customValidationProfiles").map(seqToSet).getOrElse(Set.empty)
    val semanticExtensions: Set[String] = m.key("semanticExtensions").map(seqToSet).getOrElse(Set.empty)

    Some(DidChangeConfigurationNotificationParams(mainUri, folder, dependencies, profiles, semanticExtensions))
  }

  private def seqToSet(entry: YMapEntry): Set[String] = {
    entry.value.value match {
      case seq: YSequence => seq.nodes.map(_.toOption[String].getOrElse("")).toSet
      case _              => Set.empty
    }
  }

  override protected def runCommand(param: DidChangeConfigurationNotificationParams): Future[Unit] =
    wsc.getWorkspace(param.folder.getOrElse(param.mainUri)).map { manager =>
      if (manager.acceptsConfigUpdateByCommand) {
        logger.debug(
          s"DidChangeConfiguration for workspace @ ${manager.folderUri} (folder: ${param.folder}, mainUri:${param.mainUri})",
          "DidChangeConfigurationCommandExecutor",
          "runCommand"
        )
        wsc.contentManagerConfiguration(manager,
                                        param.mainUri,
                                        param.dependencies,
                                        param.customValidationProfiles,
                                        param.semanticExtensions,
                                        None)
      } else {
        logger.warning(
          s"Tried to change configuration of workspace `${manager.folderUri}` (folder: ${param.folder}, mainUri:${param.mainUri}) but it does not accept configuration by command",
          "DidChangeConfigurationCommandExecutor",
          "runCommand"
        )
      }
    }
}
