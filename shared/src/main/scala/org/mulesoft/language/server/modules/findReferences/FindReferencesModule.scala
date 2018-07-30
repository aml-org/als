package org.mulesoft.language.server.modules.findReferences;

import org.mulesoft.high.level.{ReferenceSearchResult, Search}
import org.mulesoft.high.level.interfaces.{IASTUnit, IProject}
import org.mulesoft.language.common.dtoTypes.{ILocation, IRange}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.SearchUtils
import org.mulesoft.language.server.modules.hlastManager.HLASTManager

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global;

class FindReferencesModule extends AbstractServerModule {
	override val moduleId: String = "FIND_REFERENCES";

	val moduleDependencies: Array[String] = Array(HLASTManager.moduleId);
	
	override def launch(): Try[IServerModule] = {
		val superLaunch = super.launch();

		if(superLaunch.isSuccess) {
			connection.onFindReferences(findReferences, false);

			Success(this);
		} else {
			superLaunch;
		}
	}
	
	def findReferences(uri: String, position: Int): Future[Seq[ILocation]] = {
		this.connection.debug(s"Finding references at position ${position}",
			"FindReferencesModule", "findReferences")

		var promise = Promise[Seq[ILocation]]();

		currentAst(uri).andThen {
			case Success(project) => {
				SearchUtils.findReferences(project, position) match {
					case Some(searchResult) => promise.success(searchResult);
					
					case _ => {
						Seq();
					}
				}
			};
			
			case Failure(error) => {
				promise.failure(error)
			};
		}

		promise.future;
	}

	private def currentAst(uri: String): Future[IProject] = {
		val hlmanager = this.getDependencyById(HLASTManager.moduleId).get.asInstanceOf[HLASTManager]

		hlmanager.forceGetCurrentAST(uri).map(ast=>{
			println("ASTFOUND: " + ast.rootASTUnit.path + " " + ast.rootASTUnit.text);
			
			ast
		})
	}
}

object FindReferencesModule {
	val moduleId: String = "FIND_REFERENCES";
}
