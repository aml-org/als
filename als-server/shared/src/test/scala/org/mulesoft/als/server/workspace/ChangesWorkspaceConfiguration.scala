package org.mulesoft.als.server.workspace

import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.lsp.textsync.KnownDependencyScopes.{CUSTOM_VALIDATION, DIALECT, SEMANTIC_EXTENSION}
import org.mulesoft.lsp.workspace.ExecuteCommandParams

import scala.concurrent.Future

trait ChangesWorkspaceConfiguration extends PlatformSecrets {
  implicit private val Platform: Platform = platform

  def changeConfigArgs(mainUri: Option[String],
                       folder: String,
                       dependencies: Set[String] = Set.empty,
                       profiles: Set[String] = Set.empty,
                       semanticExtensions: Set[String] = Set.empty,
                       dialects: Set[String] = Set.empty): String = {
    val allDeps = (dependencies.map(d => s""""$d"""") ++
      profiles.map(p => s"""{"file": "$p", "scope": "$CUSTOM_VALIDATION"}""") ++
      semanticExtensions.map(s => s"""{"file": "$s", "scope": "$SEMANTIC_EXTENSION"}""") ++
      dialects.map(d => s"""{"file": "$d", "scope": "$DIALECT"}""")).mkString(",")
    s"""{
        "mainUri": "${mainUri.getOrElse("")}",
        "folder": "$folder",
        "dependencies": [$allDeps]}
      """
  }

  def changeWorkspaceConfiguration(server: LanguageServer)(args: String): Future[AnyRef] = {
    server.workspaceService.executeCommand(ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION, List(args)))
  }
}
