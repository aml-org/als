package org.mulesoft.als.server.modules.actions.rename

import amf.core.internal.remote.Platform
import org.mulesoft.als.actions.definition.FindDefinition
import org.mulesoft.als.actions.rename.FindRenameLocations
import org.mulesoft.als.actions.renamefile.RenameFileAction
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.workspace.CompilableUnit
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.rename.{RenameParams, RenameRequestType}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RenameHandler(
    workspace: WorkspaceManager,
    configurationReader: AlsConfigurationReader,
    platform: Platform
) extends TelemeteredRequestHandler[RenameParams, WorkspaceEdit]
    with RenameTools {
  override def `type`: RenameRequestType.type = RenameRequestType

  override def task(params: RenameParams): Future[WorkspaceEdit] =
    rename(
      params.textDocument.uri,
      Position(params.position.line, params.position.character),
      params.newName,
      uuid(params)
    )

  override protected def telemetry: TelemetryProvider = Logger.delegateTelemetryProvider.get

  override protected def code(params: RenameParams): String = "RenameManager"

  override protected def beginType(params: RenameParams): MessageTypes = MessageTypes.BEGIN_RENAME

  override protected def endType(params: RenameParams): MessageTypes = MessageTypes.END_RENAME

  override protected def msg(params: RenameParams): String = s"Request for renaming on ${params.textDocument.uri}"

  override protected def uri(params: RenameParams): String = params.textDocument.uri

  def rename(uri: String, position: Position, newName: String, uuid: String): Future[WorkspaceEdit] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .flatMap(withIsAliases(_, uri, uuid, position, workspace))
      .flatMap(t => {
        val (bu, isAliasDeclaration) = t
        if (isAliasDeclaration || isDeclarableKey(bu, position, uri))
          FindRenameLocations
            .changeDeclaredName(
              uri,
              position,
              newName,
              workspace.getAliases(uri, uuid),
              workspace.getRelationships(uri, uuid).map(_._2),
              bu.astPartBranch,
              bu.unit
            )
            .map(_.toWorkspaceEdit(configurationReader.supportsDocumentChanges))
        else if (renameThroughReferenceEnabled) // enable when polished, add logic to prepare rename
          for {
            fromLinks <- renameFromLink(
              uri,
              position,
              newName,
              uuid,
              bu,
              workspace
            ) // if Some() then it's a link to a file
            fromDef <- renameFromDefinition(
              uri,
              position,
              newName,
              uuid,
              bu
            ) // if Some() then it's a reference to a declaration
          } yield {
            (fromLinks orElse fromDef) getOrElse WorkspaceEdit.empty // if none of the above, return empty
          }
        else Future.successful(WorkspaceEdit.empty)
      })

  private def renameFromLink(
      uri: String,
      position: Position,
      newName: String,
      uuid: String,
      bu: CompilableUnit,
      workspaceManager: WorkspaceManager
  ): Future[Option[WorkspaceEdit]] = {
    if (configurationReader.supportsDocumentChanges)
      for {
        links    <- workspaceManager.getDocumentLinks(uri, uuid)
        allLinks <- workspaceManager.getAllDocumentLinks(uri, uuid)
      } yield {
        Logger.debug("Got the following document links", "RenameFileActionManager", "rename")
        links.foreach { l =>
          Logger.debug(s"-> link: ${l.target}", "RenameFileActionManager", "rename")
        }
        links
          .find(l => PositionRange(l.range).contains(position))
          .map(l =>
            RenameFileAction.renameFileEdits(
              TextDocumentIdentifier(l.target),
              TextDocumentIdentifier(getUriWithNewName(l.target, newName)),
              allLinks,
              platform
            )
          )
          .map(_.toWorkspaceEdit(configurationReader.supportsDocumentChanges))
      }
    else Future.successful(None)
  }

  // todo: re-check when moving paths is available
  private def getUriWithNewName(target: String, newName: String): String =
    s"${splitUriName(target)._1}${splitUriName(newName)._2}"

  private def splitUriName(target: String) =
    target.splitAt(target.lastIndexOf('/') + 1)

  private def renameFromDefinition(
      uri: String,
      position: Position,
      newName: String,
      uuid: String,
      bu: CompilableUnit
  ): Future[Option[WorkspaceEdit]] =
    FindDefinition
      .getDefinition(
        uri,
        position,
        workspace.getRelationships(uri, uuid).map(_._2),
        workspace.getAliases(uri, uuid),
        bu.astPartBranch
      )
      .flatMap(_.headOption match {
        case Some(definition) =>
          FindRenameLocations
            .changeDeclaredName(
              definition.targetUri,
              Position(definition.targetRange.start),
              newName,
              workspace.getAliases(uri, uuid),
              workspace.getRelationships(uri, uuid).map(_._2),
              bu.astPartBranch,
              bu.unit
            )
            .map(a => Some(a.toWorkspaceEdit(configurationReader.supportsDocumentChanges)))
        case _ =>
          Future.successful(None)
      })

  /** If Some(_), this will be sent as a response as a default for a managed exception
    */
  override protected val empty: Option[WorkspaceEdit] = Some(WorkspaceEdit(None, None))
}
