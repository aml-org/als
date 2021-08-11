package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.references.FindReferences
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.relationships.LinkTypes
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.{Location, LocationLink}
import org.mulesoft.lsp.feature.implementation.{
  ImplementationClientCapabilities,
  ImplementationConfigType,
  ImplementationParams,
  ImplementationRequestType
}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoToImplementationManager(val workspace: WorkspaceManager,
                                private val telemetryProvider: TelemetryProvider,
                                private val logger: Logger)
    extends RequestModule[ImplementationClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] {

  private var conf: Option[ImplementationClientCapabilities] = None

  override val `type`: ConfigType[ImplementationClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] =
    ImplementationConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[ImplementationParams, Either[Seq[Location], Seq[LocationLink]]] {
      override def `type`: ImplementationRequestType.type =
        ImplementationRequestType

      override def task(params: ImplementationParams): Future[Either[Seq[Location], Seq[LocationLink]]] =
        goToImplementation(params.textDocument.uri, LspRangeConverter.toPosition(params.position), uuid(params))

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: ImplementationParams): String = "GotoImplementationManager"

      override protected def beginType(params: ImplementationParams): MessageTypes = MessageTypes.BEGIN_GOTO_IMPL

      override protected def endType(params: ImplementationParams): MessageTypes = MessageTypes.END_GOTO_IMPL

      override protected def msg(params: ImplementationParams): String =
        s"request for go to implementation on ${params.textDocument.uri}"

      override protected def uri(params: ImplementationParams): String = params.textDocument.uri

      /**
        * If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Either[Seq[Location], Seq[LocationLink]]] = Some(Right(Seq()))
    }
  )

  override def applyConfig(
      config: Option[ImplementationClientCapabilities]): Either[Boolean, WorkDoneProgressOptions] = {
    conf = config
    Left(true)
  }

  def goToImplementation(uri: String,
                         position: Position,
                         uuid: String): Future[Either[Seq[Location], Seq[LocationLink]]] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .flatMap(cu => {
        FindReferences
          .getReferences(
            uri,
            position,
            workspace
              .getAliases(uri, uuid),
            workspace
              .getRelationships(uri, uuid)
              .map(_._2.filter(_.linkType == LinkTypes.TRAITRESOURCES)),
            cu.yPartBranch
          )
          .map(_.map(_.source))

      })
      .map(Left(_))

  override def initialize(): Future[Unit] = Future.successful()

}
