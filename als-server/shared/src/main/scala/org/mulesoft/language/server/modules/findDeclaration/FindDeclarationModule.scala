package org.mulesoft.language.server.modules.findDeclaration

import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.ILocation
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.modules.SearchUtils
import org.mulesoft.language.server.modules.hlastManager.HlAstManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class FindDeclarationModule extends AbstractServerModule {
  override val moduleId: String = "FIND_DECLARATION"

  val moduleDependencies: Array[String] = Array(HlAstManager.moduleId)

  override def launch(): Future[Unit] =
    super.launch()
      .map(_ => {
        connection.onOpenDeclaration(findDeclaration, false)
      })

  private def findDeclaration(_uri: String, position: Int): Future[Seq[ILocation]] = {
    val uri = PathRefine.refinePath(_uri, platform)
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
    getDependencyById(HlAstManager.moduleId).get.asInstanceOf[HlAstManager].forceGetCurrentAST(uri)
  }
}

object FindDeclarationModule {
  val moduleId: String = "FIND_DECLARATION"
}
