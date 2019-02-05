package org.mulesoft.language.server.modules.rename

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.{IChangedDocument, ITextEdit}
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.SearchUtils
import org.mulesoft.language.server.modules.hlastManager.HLASTmanager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

private class TextIssue(var label: String, var start: Int, var end: Int)

class RenameModule extends AbstractServerModule {
  override val moduleId: String = "RENAME"

  val moduleDependencies: Array[String] = Array(HLASTmanager.moduleId)

  override def launch(): Try[IServerModule] = {
    val superLaunch = super.launch()

    if (superLaunch.isSuccess) {
      connection.onRename(findTargets)

      Success(this)
    } else {
      superLaunch
    }
  }

  private def findTargets(_uri: String, position: Int, newName: String): Future[Seq[IChangedDocument]] = {
    val uri     = PathRefine.refinePath(_uri, platform)
    val promise = Promise[Seq[IChangedDocument]]()

    currentAst(uri).andThen {
      case Success(project) => {
        SearchUtils.findAll(project, position) match {
          case Some(found) =>
            promise.success(found.map(location =>
              IChangedDocument(location.uri, 0, None, Some(Seq(ITextEdit(location.range, newName))))))

          case _ => promise.success(Seq())
        }
      }

      case Failure(error) => promise.failure(error)
    }

    promise.future
  }

  private def currentAst(uri: String): Future[IProject] = {
    val hlmanager = this.getDependencyById(HLASTmanager.moduleId).get.asInstanceOf[HLASTmanager]

    hlmanager.forceGetCurrentAST(uri)
  }
}

object RenameModule {
  val moduleId: String = "RENAME"
}
