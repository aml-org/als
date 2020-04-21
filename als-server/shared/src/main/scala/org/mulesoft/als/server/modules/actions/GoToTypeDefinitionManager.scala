package org.mulesoft.als.server.modules.actions

import java.util.UUID

import amf.core.remote.Platform
import org.mulesoft.als.actions.common.LinkTypes
import org.mulesoft.als.actions.definition.FindDefinition
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.StaticRegistrationOptions
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Location, LocationLink, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.feature.typedefinition.{
  TypeDefinitionClientCapabilities,
  TypeDefinitionConfigType,
  TypeDefinitionRequestType
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoToTypeDefinitionManager(val workspace: WorkspaceManager,
                                platform: Platform,
                                private val telemetryProvider: TelemetryProvider,
                                private val logger: Logger)
    extends RequestModule[TypeDefinitionClientCapabilities, Either[Boolean, StaticRegistrationOptions]] {

  private var conf: Option[TypeDefinitionClientCapabilities] = None

  override val `type`: ConfigType[TypeDefinitionClientCapabilities, Either[Boolean, StaticRegistrationOptions]] =
    TypeDefinitionConfigType

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[TextDocumentPositionParams, Either[Seq[Location], Seq[LocationLink]]] {
      override def `type`: TypeDefinitionRequestType.type =
        TypeDefinitionRequestType

      override def apply(params: TextDocumentPositionParams): Future[Either[Seq[Location], Seq[LocationLink]]] =
        goToTypeDefinition(params.textDocument.uri, LspRangeConverter.toPosition(params.position))
    }
  )

  override def applyConfig(
      config: Option[TypeDefinitionClientCapabilities]): Either[Boolean, StaticRegistrationOptions] = {
    conf = config
    Left(true)
  }

  def goToTypeDefinition(uri: String, position: Position): Future[Either[Seq[Location], Seq[LocationLink]]] = {
    val uuid = UUID.randomUUID().toString
    FindDefinition
      .getDefinition(
        uri,
        position,
        workspace.getRelationships(uri, uuid).map(_.filter(_.linkType == LinkTypes.TRAITRESOURCES)),
        workspace.getAliases(uri, uuid),
        workspace.getLastUnit(uri, uuid).map(_.unit),
        platform
      )
      .map(Right(_))
  }

  override def initialize(): Future[Unit] = Future.successful()

}
