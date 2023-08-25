package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.definition.FindDefinition
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.{Location, LocationLink}
import org.mulesoft.lsp.feature.definition.{
  DefinitionClientCapabilities,
  DefinitionConfigType,
  DefinitionParams,
  DefinitionRequestType
}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoToDefinitionManager(
    val workspace: WorkspaceManager,
    private val telemetryProvider: TelemetryProvider
) extends RequestModule[DefinitionClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] {

  private var conf: Option[DefinitionClientCapabilities] = None

  override val `type`: ConfigType[DefinitionClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] =
    DefinitionConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[DefinitionParams, Either[Seq[Location], Seq[LocationLink]]] {
      override def `type`: DefinitionRequestType.type = DefinitionRequestType

      override def task(params: DefinitionParams): Future[Either[Seq[Location], Seq[LocationLink]]] =
        goToDefinition(params.textDocument.uri, LspRangeConverter.toPosition(params.position), uuid(params))

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: DefinitionParams): String = "GotoDefinitionManager"

      override protected def beginType(params: DefinitionParams): MessageTypes = MessageTypes.BEGIN_GOTO_DEF

      override protected def endType(params: DefinitionParams): MessageTypes = MessageTypes.END_GOTO_DEF

      override protected def msg(params: DefinitionParams): String =
        s"Request for go to definition on ${params.textDocument.uri}"

      override protected def uri(params: DefinitionParams): String = params.textDocument.uri

      /** If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Either[Seq[Location], Seq[LocationLink]]] = Some(Right(Seq()))
    }
  )

  override def applyConfig(config: Option[DefinitionClientCapabilities]): Either[Boolean, WorkDoneProgressOptions] = {
    conf = config
    Left(true)
  }

  private def goToDefinition(
      uri: String,
      position: Position,
      uuid: String
  ): Future[Either[Seq[Location], Seq[LocationLink]]] =
    for {
      unit <- workspace.getLastUnit(uri, uuid)
      workspaceDefinitions <- FindDefinition
        .getDefinition(
          uri,
          position,
          workspace.getRelationships(uri, uuid).map(_._2),
          workspace.getAliases(uri, uuid),
          unit.astPartBranch
        )
    } yield {
      Right(workspaceDefinitions)
    }

  override def initialize(): Future[Unit] = Future.successful()

}
