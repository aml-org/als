package org.mulesoft.als.server.modules.actions

import org.mulesoft.amfintegration.relationships.LinkTypes
import org.mulesoft.als.actions.definition.FindDefinition
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.configuration.StaticRegistrationOptions
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.common.{Location, LocationLink}
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.mulesoft.lsp.feature.typedefinition.{
  TypeDefinitionClientCapabilities,
  TypeDefinitionConfigType,
  TypeDefinitionParams,
  TypeDefinitionRequestType
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoToTypeDefinitionManager(val workspace: WorkspaceManager,
                                private val telemetryProvider: TelemetryProvider,
                                private val logger: Logger)
    extends RequestModule[TypeDefinitionClientCapabilities, Either[Boolean, StaticRegistrationOptions]] {

  private var conf: Option[TypeDefinitionClientCapabilities] = None

  override val `type`: ConfigType[TypeDefinitionClientCapabilities, Either[Boolean, StaticRegistrationOptions]] =
    TypeDefinitionConfigType

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[TypeDefinitionParams, Either[Seq[Location], Seq[LocationLink]]] {
      override def `type`: TypeDefinitionRequestType.type =
        TypeDefinitionRequestType

      override def task(params: TypeDefinitionParams): Future[Either[Seq[Location], Seq[LocationLink]]] =
        goToTypeDefinition(params.textDocument.uri, LspRangeConverter.toPosition(params.position), uuid(params))

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: TypeDefinitionParams): String =
        "GotoTypeDefinitionManager"

      override protected def beginType(params: TypeDefinitionParams): MessageTypes =
        MessageTypes.BEGIN_GOTO_T_DEF

      override protected def endType(params: TypeDefinitionParams): MessageTypes =
        MessageTypes.END_GOTO_T_DEF

      override protected def msg(params: TypeDefinitionParams): String =
        s"request for go to type definition on ${params.textDocument.uri}"

      override protected def uri(params: TypeDefinitionParams): String =
        params.textDocument.uri

      /**
        * If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Either[Seq[Location], Seq[LocationLink]]] = Some(Right(Seq()))
    }
  )

  override def applyConfig(
      config: Option[TypeDefinitionClientCapabilities]): Either[Boolean, StaticRegistrationOptions] = {
    conf = config
    Left(true)
  }

  def goToTypeDefinition(uri: String,
                         position: Position,
                         uuid: String): Future[Either[Seq[Location], Seq[LocationLink]]] =
    workspace
      .getLastUnit(uri, uuid)
      .flatMap(
        unit =>
          FindDefinition
            .getDefinition(
              uri,
              position,
              workspace
                .getRelationships(uri, uuid)
                .map(_._2.filter(_.linkType == LinkTypes.TRAITRESOURCES)),
              workspace.getAliases(uri, uuid),
              unit.yPartBranch
          ))
      .map(Right(_))

  override def initialize(): Future[Unit] = Future.successful()
}
