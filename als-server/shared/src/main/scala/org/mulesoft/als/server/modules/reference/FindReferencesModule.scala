package org.mulesoft.als.server.modules.reference

import common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.{LspConverter, SearchUtils}
import org.mulesoft.als.server.modules.common.interfaces.ILocation
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.common.Location
import org.mulesoft.lsp.feature.reference.{
  ReferenceClientCapabilities,
  ReferenceConfigType,
  ReferenceParams,
  ReferenceRequestType
}
import org.mulesoft.lsp.feature.{RequestHandler, RequestType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class FindReferencesModule(private val hlAstManager: HlAstManager,
                           private val platform: AlsPlatform,
                           private val logger: Logger)
    extends RequestModule[ReferenceClientCapabilities, Unit] {

  override val `type`: ConfigType[ReferenceClientCapabilities, Unit] = ReferenceConfigType

  override def applyConfig(config: Option[ReferenceClientCapabilities]): Unit = {}

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[ReferenceParams, Seq[Location]] {
      override def `type`: RequestType[ReferenceParams, Seq[Location]] = ReferenceRequestType

      override def apply(params: ReferenceParams): Future[Seq[Location]] = {
        findReferences(params.textDocument.uri, LspConverter.toPosition(params.position))
          .map(_.map(LspConverter.toLspLocation))
      }
    }
  )

  override def initialize(): Future[Unit] = Future.successful()

  def findReferences(uri: String, position: Position): Future[Seq[ILocation]] = {
    val refinedUri = PathRefine.refinePath(uri, platform)
    logger.debug(s"Finding references at position $position", "FindReferencesModule", "findReferences")

    val promise = Promise[Seq[ILocation]]()

    currentAst(uri).andThen {
      case Success(project) =>
        SearchUtils.findReferences(project, position.offset(project.units(refinedUri).text)) match {
          case Some(searchResult) => promise.success(searchResult)
          case _                  => Seq()
        }

      case Failure(error) => promise.failure(error)
    }

    promise.future
  }

  private def currentAst(uri: String): Future[IProject] = {
    hlAstManager.forceGetCurrentAST(uri)
  }
}
