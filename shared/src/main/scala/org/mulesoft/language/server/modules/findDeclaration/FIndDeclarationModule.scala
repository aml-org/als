package org.mulesoft.language.server.modules.findDeclaration;

import org.mulesoft.high.level.Search;
import org.mulesoft.high.level.interfaces.IProject;
import org.mulesoft.language.common.dtoTypes.{ILocation, IRange};
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule};
import org.mulesoft.language.server.modules.hlastManager.HLASTManager;

import scala.concurrent.{Future, Promise};
import scala.util.{Failure, Success, Try};

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
			case Success(project) => promise.success(Search.findDefinitionByPosition(project.rootASTUnit, position) match {
				case Some(searchResult) => Seq(searchResult.definition).map(_.sourceInfo.ranges.headOption).filter(_ match {
					case Some(range) => range.start.resolved;

					case _ => false;
				}).map(lowLevelRange => {
					var unit = searchResult.definition.astUnit;

					var start = lowLevelRange.get.start.position;
					var end = start + unit.text.substring(start).indexOf(":");

					new ILocation {
						var range: IRange = new IRange(start, end);

						var uri: String = unit.path.replace("file:///", "/");

						var version: Int = -1;
					}});

				case _ => Seq();
			});

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
