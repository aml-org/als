package org.mulesoft.als.server.modules.definition

import amf.core.remote.Platform
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.interfaces.ILocation
import org.mulesoft.als.server.modules.common.{LspConverter, SearchUtils}
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.common.{Location, LocationLink, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.definition.{DefinitionClientCapabilities, DefinitionConfigType, DefinitionRequestType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class DefinitionModule(private val hlAstManager: HlAstManager,
                       private val logger: Logger,
                       private val platform: Platform)
    extends RequestModule[DefinitionClientCapabilities, Unit] {

  override val `type`: ConfigType[DefinitionClientCapabilities, Unit] = DefinitionConfigType

  override def initialize(): Future[Unit] = Future.successful()

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[TextDocumentPositionParams, Either[Seq[Location], Seq[LocationLink]]] {
      override def `type`: DefinitionRequestType.type = DefinitionRequestType

      override def apply(params: TextDocumentPositionParams): Future[Either[Seq[Location], Seq[LocationLink]]] = {
          findDeclaration(params.textDocument.uri, LspConverter.toPosition(params.position))
            .map(_.map(LspConverter.toLspLocation))
            .map(Left(_))
        }
    }
  )

  override def applyConfig(config: Option[DefinitionClientCapabilities]): Unit = {}

  private def findDeclaration(uri: String, position: Position): Future[Seq[ILocation]] = {
    val promise = Promise[Seq[ILocation]]()

    currentAst(uri) andThen {
      case Success(project) =>
        SearchUtils.findDeclaration(project, position) match {
          case Some(result) => promise.success(result)

          case _ => promise.success(Seq())
        }

      case Failure(error) => promise.failure(error)
    }

    promise.future
  }

  private def currentAst(uri: String): Future[IProject] =
    hlAstManager.forceGetCurrentAST(uri)
}
