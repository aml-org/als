package org.mulesoft.language.server.modules.findReferences;

import org.mulesoft.high.level.{ReferenceSearchResult, Search}
import org.mulesoft.high.level.interfaces.{IASTUnit, IProject}
import org.mulesoft.language.common.dtoTypes.{ILocation, IRange}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.hlastManager.HLASTManager

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global;

private class TextIssue(var label: String, var start: Int, var end: Int);

class FindReferencesModule extends AbstractServerModule {
	override val moduleId: String = "FIND_REFERENCES";

	val moduleDependencies: Array[String] = Array(HLASTManager.moduleId);

	private var breakers = Seq(",", ".", ";", ":", " ", "\"", "'","{", "}", "[", "]", "/", "|", "\n", "\t", "\r").map(_.charAt(0));

	override def launch(): Try[IServerModule] = {
		val superLaunch = super.launch();

		if(superLaunch.isSuccess) {
			connection.onFindReferences(findReferences, false);

			Success(this);
		} else {
			superLaunch;
		}
	}

	private def findTextIssue(content: String, position: Int): TextIssue = {
		var start = position;
		var end = position;

		if(isBreaker(end, content)) {
			end = end - 1;
		}

		if(isBreaker(start, content)) {
			start = end;
		}

		while(start > 0 && !isBreaker(start, content)) {
			start = start - 1;
		}

		while(end < content.length && !isBreaker(end, content)) {
			end = end + 1;
		}

		start = start + 1;

		return new TextIssue(content.substring(start, end), start, end);
	}

	private def findTextIssues(content: String, label: String, searchStart: Int, searchEnd: Int): Seq[TextIssue] = {
		var issues: ListBuffer[TextIssue] = ListBuffer();

		var index = content.indexOf(label, searchStart);

		while(index > -1 && index < searchEnd) {
			issues += findTextIssue(content, index);

			index = content.indexOf(label, index + 1);
		}

		return issues.filter(issue => issue.label == label).distinct;
	}

	private def isBreaker(index: Int, content: String): Boolean = breakers.contains(content.charAt(index));
	
	private def findReferencesByPosition(unit: IASTUnit, position: Int): Option[ReferenceSearchResult] = {
		var res = Search.findReferencesByPosition(unit, position);
		
		res;
	}
	
	def findReferences(uri: String, position: Int): Future[Seq[ILocation]] = {
		this.connection.debug(s"Finding references at position ${position}",
			"FindReferencesModule", "findReferences")

		var promise = Promise[Seq[ILocation]]();

		currentAst(uri).andThen {

			case Success(project) => {
				findReferencesByPosition(project.rootASTUnit, position) match {
					case Some(searchResult) => {
						var result: ListBuffer[ILocation] = ListBuffer();

						var nodes = searchResult.references.filter(_.sourceInfo.ranges.headOption match {
							case Some(range) => range.start.resolved;

							case _ => false;
						});

						var issueName = findTextIssue(project.rootASTUnit.text, position).label;

						nodes.foreach(node => {
							var nodeRange = node.sourceInfo.ranges.headOption.get;

							findTextIssues(node.astUnit.text, issueName, nodeRange.start.position, nodeRange.end.position).foreach(issue => result += new ILocation {
								var range: IRange = new IRange(issue.start, issue.end);

								var uri: String = node.astUnit.path.replace("file:///", "/");

								var version: Int = -1;

								override def toString: String = uri + "_" + issue.start + "_" + issue.end;

								override def equals(obj: scala.Any): Boolean = toString().equals(obj.toString());

								override def hashCode(): Int = toString().hashCode();
							});
						});

						promise.success(result.sortWith((l1, l2) => l1.range.start < l2.range.start));
					};

					case _ => {
						Seq()
					};
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
