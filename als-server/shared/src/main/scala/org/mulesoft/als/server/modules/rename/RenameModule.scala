package org.mulesoft.als.server.modules.rename

import common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.{LspConverter, SearchUtils, TextEdit}
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.textsync.ChangedDocument
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.lsp.edit.WorkspaceEdit
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.rename._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

private class TextIssue(var label: String, var start: Int, var end: Int)

class RenameModule(private val hlAstManager: HlAstManager,
                   private val platform: AlsPlatform,
                   private val logger: Logger)
  extends RequestModule[RenameClientCapabilities, RenameOptions] {

  override val `type`: RenameConfigType.type = RenameConfigType

  override def applyConfig(config: Option[RenameClientCapabilities]): RenameOptions = RenameOptions()

  override def getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[RenameParams, WorkspaceEdit] {
      override def `type`: RenameRequestType.type = RenameRequestType

      override def apply(params: RenameParams): Future[WorkspaceEdit] =
        findTargets(params.textDocument.uri, LspConverter.toPosition(params.position), params.newName)
          .map(LspConverter.toWorkspaceEdit)
    }
  )

  override def initialize(): Future[Unit] = Future.successful()

  private def findTargets(_uri: String, position: Position, newName: String): Future[Seq[ChangedDocument]] = {
    val uri = PathRefine.refinePath(_uri, platform)
    val promise = Promise[Seq[ChangedDocument]]()

    currentAst(uri).andThen {
      case Success(project) => {
        SearchUtils.findAll(project, position) match {
          case Some(found) =>
            promise.success(found.map(location =>
              ChangedDocument(location.uri, 0, None, Some(Seq(TextEdit(location.posRange, newName))))))

          case _ => promise.success(Seq())
        }
      }

      case Failure(error) => promise.failure(error)
    }

    promise.future
  }

  private def currentAst(uri: String): Future[IProject] = {
    hlAstManager.forceGetCurrentAST(uri)
  }
}
