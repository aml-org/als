package org.mulesoft.language.server.modules.findReferences

import common.dtoTypes.Position
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.ILocation
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.modules.SearchUtils
import org.mulesoft.language.server.modules.hlastManager.HlAstManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class FindReferencesModule extends AbstractServerModule {
  override val moduleId: String = "FIND_REFERENCES"

  val moduleDependencies: Array[String] = Array(HlAstManager.moduleId)

  override def launch(): Future[Unit] =
    super
      .launch()
      .map(_ => {
        connection.onFindReferences(findReferences, false)
      })

  def findReferences(_uri: String, position: Position): Future[Seq[ILocation]] = {
    val uri = PathRefine.refinePath(_uri, platform)
    this.connection.debug(s"Finding references at position $position", "FindReferencesModule", "findReferences")

    val promise = Promise[Seq[ILocation]]()

    currentAst(uri).andThen {
      case Success(project) =>
        SearchUtils.findReferences(project, position.offset(project.units(uri).text)) match {
          case Some(searchResult) => promise.success(searchResult)
          case _                  => Seq()
        }

      case Failure(error) => promise.failure(error)
    }

    promise.future
  }

  private def currentAst(uri: String): Future[IProject] = {
    val hlmanager = this.getDependencyById(HlAstManager.moduleId).get.asInstanceOf[HlAstManager]

    hlmanager
      .forceGetCurrentAST(uri)
      .map(ast => {
        println("ASTFOUND: " + ast.rootASTUnit.path + " " + ast.rootASTUnit.text)

        ast
      })
  }
}

object FindReferencesModule {
  val moduleId: String = "FIND_REFERENCES"
}
