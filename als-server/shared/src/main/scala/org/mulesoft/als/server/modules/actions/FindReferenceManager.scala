package org.mulesoft.als.server.modules.actions

import org.mulesoft.als.actions.references.FindReferences
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.WorkDoneProgressOptions
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

class FindReferenceManager(
    val workspace: WorkspaceManager
) extends RequestModule[ReferenceClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] {

  private var conf: Option[ReferenceClientCapabilities] = None

  override val `type`: ConfigType[ReferenceClientCapabilities, Either[Boolean, WorkDoneProgressOptions]] =
    ReferenceConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[ReferenceParams, Seq[Location]] {
      override def `type`: ReferenceRequestType.type = ReferenceRequestType

      override def task(params: ReferenceParams): Future[Seq[Location]] =
        findReference(params.textDocument.uri, Position(params.position), uuid(params))

      override protected def telemetry: TelemetryProvider = Logger.delegateTelemetryProvider.get

      override protected def code(params: ReferenceParams): String = "FindReferenceManager"

      override protected def beginType(params: ReferenceParams): MessageTypes = MessageTypes.BEGIN_FIND_REF

      override protected def endType(params: ReferenceParams): MessageTypes = MessageTypes.END_FIND_REF

      override protected def msg(params: ReferenceParams): String =
        s"Request for references on ${params.textDocument.uri}"

      override protected def uri(params: ReferenceParams): String = params.textDocument.uri

      /** If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Seq[Location]] = Some(Seq())
    }
  )

  override def applyConfig(config: Option[ReferenceClientCapabilities]): Either[Boolean, WorkDoneProgressOptions] = {
    conf = config
    Left(true)
  }

  def findReference(uri: String, position: Position, uuid: String): Future[Seq[Location]] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(cu => {
        FindReferences
          .getReferences(
            uri,
            position,
            workspace.getAliases(uri, uuid),
            workspace.getRelationships(uri, uuid).map(_._2),
            cu.astPartBranch
          )
          .map(_.map(_.source))
      })

  override def initialize(): Future[Unit] =
    Future.successful()

}
