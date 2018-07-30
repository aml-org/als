package org.mulesoft.language.server.modules.findDeclaration;

import org.mulesoft.high.level.Search
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.{ILocation, IRange}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.SearchUtils
import org.mulesoft.language.server.modules.hlastManager.HLASTManager

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global;

class FIndDeclarationModule  extends AbstractServerModule {
	override val moduleId: String = "FIND_DECLARATION";

	val moduleDependencies: Array[String] = Array(HLASTManager.moduleId);

	override def launch(): Try[IServerModule] = {
		val superLaunch = super.launch();

		if(superLaunch.isSuccess) {
			connection.onOpenDeclaration(findDeclaration, false);

			Success(this);
		} else {
			superLaunch;
		}
	}

	private def findDeclaration(uri: String, position: Int): Future[Seq[ILocation]] = {
		var promise = Promise[Seq[ILocation]]();
		
		currentAst(uri) andThen {
			case Success(project) => SearchUtils.findDeclaration(project, position) match {
				case Some(result) => promise.success(result);

				case _ => promise.success(Seq());
			};
			
			case Failure(error) => promise.failure(error);
		}
		
		promise.future;
	}

	private def currentAst(uri: String): Future[IProject] = {
		getDependencyById(HLASTManager.moduleId).get.asInstanceOf[HLASTManager].forceGetCurrentAST(uri);
	}
}

object FIndDeclarationModule {
	val moduleId: String = "FIND_DECLARATION";
}
