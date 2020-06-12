package org.mulesoft.als.server.modules.actions

import java.util.UUID

import amf.core.remote.Platform
import org.mulesoft.als.actions.common.LinkTypes
import org.mulesoft.als.actions.references.FindReferences
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.StaticRegistrationOptions
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Location, LocationLink}
import org.mulesoft.lsp.feature.implementation.{
  ImplementationClientCapabilities,
  ImplementationConfigType,
  ImplementationParams,
  ImplementationRequestType
}
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoToImplementationManager(val workspace: WorkspaceManager,
                                platform: Platform,
                                private val telemetryProvider: TelemetryProvider,
                                private val logger: Logger)
    extends RequestModule[ImplementationClientCapabilities, Either[Boolean, StaticRegistrationOptions]] {

  private var conf: Option[ImplementationClientCapabilities] = None

  override val `type`: ConfigType[ImplementationClientCapabilities, Either[Boolean, StaticRegistrationOptions]] =
    ImplementationConfigType

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[ImplementationParams, Either[Seq[Location], Seq[LocationLink]]] {
      override def `type`: ImplementationRequestType.type =
        ImplementationRequestType

      override def apply(params: ImplementationParams): Future[Either[Seq[Location], Seq[LocationLink]]] =
        goToImplementation(params.textDocument.uri, LspRangeConverter.toPosition(params.position))
    }
  )

  override def applyConfig(
      config: Option[ImplementationClientCapabilities]): Either[Boolean, StaticRegistrationOptions] = {
    conf = config
    Left(true)
  }

  def goToImplementation(uri: String, position: Position): Future[Either[Seq[Location], Seq[LocationLink]]] = {
    val uuid = UUID.randomUUID().toString
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .flatMap(bu => {
        FindReferences.getReferences(bu.unit,
                                     uri,
                                     position,
                                     workspace
                                       .getRelationships(uri, uuid)
                                       .map(_.filter(_.linkType == LinkTypes.TRAITRESOURCES)))
      })
      .map(Left(_))
  }

  override def initialize(): Future[Unit] = Future.successful()

}
