package org.mulesoft.als.server.modules.actions

import java.util.UUID

import org.mulesoft.als.actions.definition.FindDefinition
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.common.{Location, LocationLink, TextDocumentPositionParams}
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.definition.{DefinitionClientCapabilities, DefinitionConfigType, DefinitionRequestType}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider
import org.mulesoft.lsp.server.LanguageServerSystemConf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoToDefinitionManager(val unitsRepository: WorkspaceManager,
                            private val telemetryProvider: TelemetryProvider,
                            private val logger: Logger,
                            private val configuration: LanguageServerSystemConf)
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

  def goToDefinition(str: String, position: Position): Future[Either[Seq[Location], Seq[LocationLink]]] = {
    unitsRepository
      .getUnit(str, UUID.randomUUID().toString)
      .map(cu => {
        FindDefinition.getDefinition(cu.unit, position, configuration.platform)
      })
      .map(Right(_))
  }

  override def initialize(): Future[Unit] = Future.successful()

}
