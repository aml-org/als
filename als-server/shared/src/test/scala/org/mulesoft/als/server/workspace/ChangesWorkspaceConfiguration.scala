package org.mulesoft.als.server.workspace

import amf.core.internal.remote.Platform
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.als.common.URIImplicits.StringUriImplicits
import org.mulesoft.lsp.workspace.ExecuteCommandParams

import scala.concurrent.Future

trait ChangesWorkspaceConfiguration extends PlatformSecrets {
  implicit private val Platform: Platform = platform

  def wrapJson(mainUri: String,
               folder: Option[String] = None,
               dependencies: Set[String] = Set.empty,
               profiles: Set[String] = Set.empty,
               semanticExtensions: Set[String] = Set.empty): String =
    s"""{"mainUri": "${mainUri.toAmfUri}", ${folder
      .map(s => s""""folder":"$s",""")
      .getOrElse("")}
      "dependencies": [${toList(dependencies)}],
      "customValidationProfiles": [${toList(profiles)}],
      "semanticExtensions": [${toList(semanticExtensions)}]}
      """

  private def toList(s: Set[String]): String = s.map(e => s""""$e"""").mkString(",")

  def changeWorkspaceConfiguration(workspaceManager: WorkspaceManager, args: String): Future[AnyRef] =
    workspaceManager.executeCommand(ExecuteCommandParams("didChangeConfiguration", List(args)))
}
