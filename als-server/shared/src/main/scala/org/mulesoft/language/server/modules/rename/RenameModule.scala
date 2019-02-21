package org.mulesoft.language.server.modules.rename

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.{ChangedDocument, TextEdit}
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.modules.SearchUtils
import org.mulesoft.language.server.modules.hlastManager.HlAstManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

private class TextIssue(var label: String, var start: Int, var end: Int)

class RenameModule extends AbstractServerModule {
  override val moduleId: String = "RENAME"

  val moduleDependencies: Array[String] = Array(HlAstManager.moduleId)

  override def launch(): Future[Unit] =
    super.launch()
      .map(_ => {
        connection.onRename(findTargets)
      })

  private def findTargets(_uri: String, position: Int, newName: String): Future[Seq[ChangedDocument]] = {
    val uri = PathRefine.refinePath(_uri, platform)
    val promise = Promise[Seq[ChangedDocument]]()

    currentAst(uri).andThen {
      case Success(project) => {
        SearchUtils.findAll(project, position) match {
          case Some(found) =>
            promise.success(found.map(location =>
              ChangedDocument(location.uri, 0, None, Some(Seq(TextEdit(location.range, newName))))))

          case _ => promise.success(Seq())
        }
      }

      case Failure(error) => promise.failure(error)
    }

    promise.future
  }

  private def currentAst(uri: String): Future[IProject] = {
    val hlmanager = this.getDependencyById(HlAstManager.moduleId).get.asInstanceOf[HlAstManager]

    hlmanager.forceGetCurrentAST(uri)
  }
}

object RenameModule {
  val moduleId: String = "RENAME"
}
