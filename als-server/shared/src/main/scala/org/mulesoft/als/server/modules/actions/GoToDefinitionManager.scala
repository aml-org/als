package org.mulesoft.als.server.modules.actions

import java.util.UUID

import amf.core.remote.Platform
import org.mulesoft.als.actions.definition.FindDefinition
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.UnitRepositoriesManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Location, LocationLink, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.definition.{DefinitionClientCapabilities, DefinitionConfigType, DefinitionRequestType}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoToDefinitionManager(val unitsRepository: UnitRepositoriesManager,
                            platform: Platform,
                            private val telemetryProvider: TelemetryProvider,
                            private val logger: Logger)
    extends RequestModule[DefinitionClientCapabilities, Unit] {

  private var conf: Option[DefinitionClientCapabilities] = None

  override val `type`: ConfigType[DefinitionClientCapabilities, Unit] =
    DefinitionConfigType

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[TextDocumentPositionParams, Either[Seq[Location], Seq[LocationLink]]] {
      override def `type`: DefinitionRequestType.type = DefinitionRequestType

      override def apply(params: TextDocumentPositionParams): Future[Either[Seq[Location], Seq[LocationLink]]] =
        goToDefinition(params.textDocument.uri, LspRangeConverter.toPosition(params.position))
    }
  )

  override def applyConfig(config: Option[DefinitionClientCapabilities]): Unit =
    conf = config

  val onGoToDefinition: (String, Position) => Future[Either[Seq[Location], Seq[LocationLink]]] = goToDefinition

  def goToDefinition(uri: String, position: Position): Future[Either[Seq[Location], Seq[LocationLink]]] = {
    val uuid = UUID.randomUUID().toString
    FindDefinition
      .getDefinition(uri,
                     position,
                     unitsRepository.getRelationships(uri, uuid),
                     unitsRepository.getAliases(uri, uuid),
                     unitsRepository.getLastCU(uri, uuid).map(_.unit),
                     platform)
      .map(Right(_))
  }

  override def initialize(): Future[Unit] = Future.successful()

}
