package org.mulesoft.als.server.workspace.command

import amf.core.internal.parser._
import org.mulesoft.als.configuration.ProjectConfiguration
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.textsync.KnownDependencyScopes._
import org.mulesoft.lsp.textsync.{DependencyConfiguration, DidChangeConfigurationNotificationParams}
import org.yaml.model.{YMap, YMapEntry, YScalar, YSequence}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DidChangeConfigurationCommandExecutor(val logger: Logger, wsc: WorkspaceManager)
    extends CommandExecutor[DidChangeConfigurationNotificationParams, Unit] {

  override protected def buildParamFromMap(m: YMap): Option[DidChangeConfigurationNotificationParams] = {
    val mainUri: Option[String] = m.key("mainUri").flatMap(e => e.value.toOption[String])
    val folder: String          = m.key("folder").flatMap(e => e.value.toOption[String]).getOrElse("")
    val dependencies: Set[Either[String, DependencyConfiguration]] =
      m.key("dependencies").map(seqToSet).getOrElse(Set.empty)

    Some(DidChangeConfigurationNotificationParams(mainUri, folder, dependencies))
  }

  private def extractDependencyConfiguration(m: YMap): DependencyConfiguration = {
    DependencyConfiguration(m.key("file").flatMap(_.value.toOption[String]).getOrElse(""),
                            m.key("scope").flatMap(_.value.toOption[String]).getOrElse(""))

  }

  private def seqToSet(entry: YMapEntry): Set[Either[String, DependencyConfiguration]] = {
    entry.value.value match {
      case seq: YSequence =>
        seq.nodes
          .map(_.value)
          .map {
            case s: YScalar => Left(s.text)
            case m: YMap    => Right(extractDependencyConfiguration(m))
          }
          .toSet
      case _ => Set.empty
    }
  }

  override protected def runCommand(param: DidChangeConfigurationNotificationParams): Future[Unit] =
    wsc.getWorkspace(param.folder).flatMap { manager =>
      logger.debug(
        s"DidChangeConfiguration for workspace @ ${manager.folderUri} (folder: ${param.folder}, mainUri:${param.mainUri})",
        "DidChangeConfigurationCommandExecutor",
        "runCommand"
      )
      val projectConfiguration = new ProjectConfiguration(
        param.folder,
        param.mainUri,
        param.dependencies
          .filterNot(d =>
            d.isRight && d.right.exists(r => Set(CUSTOM_VALIDATION, SEMANTIC_EXTENSION, DIALECT).contains(r.scope)))
          .map {
            case Left(v)  => v
            case Right(v) => v.file
          },
        extractPerScope(param, CUSTOM_VALIDATION),
        extractPerScope(param, SEMANTIC_EXTENSION),
        extractPerScope(param, DIALECT)
      )
      wsc.contentManagerConfiguration(manager, projectConfiguration)
    }

  private def extractPerScope(param: DidChangeConfigurationNotificationParams, scope: String) =
    param.dependencies
      .filter(d => d.isRight && d.right.exists(_.scope == scope))
      .map {
        case Right(v) => v.file
      }
}
