package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.references.FindReferences
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.reference.{
  ReferenceClientCapabilities,
  ReferenceConfigType,
  ReferenceParams,
  ReferenceRequestType
}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FindReferenceManager(val workspace: WorkspaceManager,
                           private val telemetryProvider: TelemetryProvider,
                           private val logger: Logger)
    extends RequestModule[ReferenceClientCapabilities, Unit] {

  private var conf: Option[ReferenceClientCapabilities] = None

  override val `type`: ConfigType[ReferenceClientCapabilities, Unit] =
    ReferenceConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[ReferenceParams, Seq[Location]] {
      override def `type`: ReferenceRequestType.type = ReferenceRequestType

      override def task(params: ReferenceParams): Future[Seq[Location]] =
        findReference(params.textDocument.uri, Position(params.position), uuid(params))

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: ReferenceParams): String = "FindReferenceManager"

      override protected def beginType(params: ReferenceParams): MessageTypes = MessageTypes.BEGIN_FIND_REF

      override protected def endType(params: ReferenceParams): MessageTypes = MessageTypes.END_FIND_REF

      override protected def msg(params: ReferenceParams): String =
        s"Request for references on ${params.textDocument.uri}"

      override protected def uri(params: ReferenceParams): String = params.textDocument.uri
    }
  )

  override def applyConfig(config: Option[ReferenceClientCapabilities]): Unit = {
    conf = config
  }

  def findReference(uri: String, position: Position, uuid: String): Future[Seq[Location]] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(_.getLast)
      .flatMap(cu => {
        FindReferences
          .getReferences(uri,
                         position,
                         workspace.getAliases(uri, uuid),
                         workspace.getRelationships(uri, uuid).map(_._2),
                         cu.yPartBranch)
          .map(_.map(_.source))
      })

  override def initialize(): Future[Unit] =
    Future.successful()

}
