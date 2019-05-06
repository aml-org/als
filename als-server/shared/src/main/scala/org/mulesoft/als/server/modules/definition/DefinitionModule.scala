package org.mulesoft.als.server.modules.definition

import common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.interfaces.ILocation
import org.mulesoft.als.server.modules.common.{LspConverter, SearchUtils}
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.common.{Location, TextDocumentPositionParams}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.definition.{DefinitionClientCapabilities, DefinitionConfigType, DefinitionRequestType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class DefinitionModule(private val hlAstManager: HlAstManager,
                       private val platform: AlsPlatform,
                       private val logger: Logger)
    extends RequestModule[DefinitionClientCapabilities, Unit] {

  override val `type`: ConfigType[DefinitionClientCapabilities, Unit] = DefinitionConfigType

  override def initialize(): Future[Unit] = Future.successful()

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[TextDocumentPositionParams, Seq[Location]] {
      override def `type`: DefinitionRequestType.type = DefinitionRequestType

      override def apply(params: TextDocumentPositionParams): Future[Seq[Location]] = {
        findDeclaration(params.textDocument.uri, LspConverter.toPosition(params.position))
          .map(_.map(LspConverter.toLspLocation))
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

  private def currentAst(uri: String): Future[IProject] = {
    hlAstManager.forceGetCurrentAST(uri)
  }
}
