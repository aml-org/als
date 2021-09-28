package org.mulesoft.als.server.workspace

import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.lsp.textsync.KnownDependencyScopes.{CUSTOM_VALIDATION, SEMANTIC_EXTENSION}
import org.mulesoft.lsp.workspace.ExecuteCommandParams

import scala.concurrent.Future

trait ChangesWorkspaceConfiguration extends PlatformSecrets {
  implicit private val Platform: Platform = platform

  def changeConfigArgs(mainUri: Option[String],
                       folder: Option[String] = None,
                       dependencies: Set[String] = Set.empty,
                       profiles: Set[String] = Set.empty,
                       semanticExtensions: Set[String] = Set.empty): String = {
    val allDeps = (dependencies.map(d => s""""$d"""") ++
      profiles.map(p => s"""{"file": "$p", "scope": "$CUSTOM_VALIDATION"}""") ++
      semanticExtensions.map(s => s"""{"file": "$s", "scope": "$SEMANTIC_EXTENSION"}""")).mkString(",")
    s"""{"mainUri": "${mainUri.map(_.toAmfUri).getOrElse("")}", ${folder
      .map(s => s""""folder":"$s",""")
      .getOrElse("")}
      "dependencies": [$allDeps]}
      """
  }

  def changeWorkspaceConfiguration(workspaceManager: WorkspaceManager, args: String): Future[AnyRef] =
    workspaceManager.executeCommand(ExecuteCommandParams("didChangeConfiguration", List(args)))
}
