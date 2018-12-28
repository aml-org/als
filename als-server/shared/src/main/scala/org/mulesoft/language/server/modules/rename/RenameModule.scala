package org.mulesoft.language.server.modules.rename

import org.mulesoft.high.level.{ReferenceSearchResult, Search}
import org.mulesoft.high.level.interfaces.{IASTUnit, IProject}
import org.mulesoft.language.common.dtoTypes.{IChangedDocument, ILocation, IRange, ITextEdit}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.SearchUtils
import org.mulesoft.language.server.modules.hlastManager.HLASTManager
import org.mulesoft.platform.PathRefine

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global;

private class TextIssue(var label: String, var start: Int, var end: Int);

class RenameModule extends AbstractServerModule {
  override val moduleId: String = "RENAME";

  val moduleDependencies: Array[String] = Array(HLASTManager.moduleId);

  override def launch(): Try[IServerModule] = {
    val superLaunch = super.launch();

    if (superLaunch.isSuccess) {
      connection.onRename(findTargets);

      Success(this);
    } else {
      superLaunch;
    }
  }

  private def findTargets(_uri: String, position: Int, newName: String): Future[Seq[IChangedDocument]] = {
    val uri     = PathRefine.refinePath(_uri, platform)
    var promise = Promise[Seq[IChangedDocument]]();

    currentAst(uri).andThen {
      case Success(project) => {
        SearchUtils.findAll(project, position) match {
          case Some(found) =>
            promise.success(found.map(location =>
              new IChangedDocument(location.uri, 0, None, Some(Seq(new ITextEdit(location.range, newName))))));

          case _ => promise.success(Seq());
        }
      }

      case Failure(error) => promise.failure(error);
    }

    promise.future;
  }

  private def currentAst(uri: String): Future[IProject] = {
    val hlmanager = this.getDependencyById(HLASTManager.moduleId).get.asInstanceOf[HLASTManager]

    hlmanager.forceGetCurrentAST(uri);
  }
}

object RenameModule {
  val moduleId: String = "RENAME";
}
