package org.mulesoft.als.server.modules.actions

import java.util.UUID

import org.mulesoft.als.actions.rename.FindRenameLocations
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.rename._
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.common.Range

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RenameManager(val workspace: WorkspaceManager,
                    private val telemetryProvider: TelemetryProvider,
                    private val logger: Logger)
    extends RequestModule[RenameClientCapabilities, RenameOptions] {

  private var conf: Option[RenameClientCapabilities] = None

  override val `type`: ConfigType[RenameClientCapabilities, RenameOptions] =
    RenameConfigType

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[RenameParams, WorkspaceEdit] {
      override def `type`: RenameRequestType.type = RenameRequestType

      override def apply(params: RenameParams): Future[WorkspaceEdit] =
        rename(params.textDocument.uri, Position(params.position.line, params.position.character), params.newName)
    },
    new RequestHandler[PrepareRenameParams, Option[Either[Range, PrepareRenameResult]]] {
      override def `type`: PrepareRenameRequestType.type = PrepareRenameRequestType

      override def apply(params: PrepareRenameParams): Future[Option[Either[Range, PrepareRenameResult]]] =
        // check if enabled?
        prepareRename(params.textDocument.uri, Position(params.position.line, params.position.character))
    }
  )

  override def applyConfig(config: Option[RenameClientCapabilities]): RenameOptions = {
    conf = config
    RenameOptions(Some(true))
  }

  def rename(uri: String, position: Position, newName: String): Future[WorkspaceEdit] = {
    val uuid = UUID.randomUUID().toString
    def innerRename() =
      workspace
        .getLastUnit(uri, uuid)
        .flatMap(_.getLast)
        .flatMap(_ => {
          FindRenameLocations.changeDeclaredName(uri, position, newName, workspace.getRelationships(uri, uuid))
        })

    telemetryProvider.timeProcess(
      "Rename",
      MessageTypes.BEGIN_RENAME,
      MessageTypes.END_RENAME,
      s"request for renaming on $uri",
      uri,
      innerRename,
      uuid
    )
  }

  def prepareRename(uri: String, position: Position): Future[Option[Either[Range, PrepareRenameResult]]] = {
    val uuid = UUID.randomUUID().toString
    def innerPrepareRename() =
      workspace
        .getLastUnit(uri, uuid)
        .flatMap(_.getLast)
        .flatMap(_ => {
          FindRenameLocations.canChangeDeclaredName(uri, position, workspace.getRelationships(uri, uuid))
        }.map(_.map(r => Left(r))))

    telemetryProvider.timeProcess(
      "Prepare rename",
      MessageTypes.BEGIN_PREP_RENAME,
      MessageTypes.END_PREP_RENAME,
      s"request for prepare renaming on $uri",
      uri,
      innerPrepareRename,
      uuid
    )
  }

  override def initialize(): Future[Unit] =
    Future.successful()
}
