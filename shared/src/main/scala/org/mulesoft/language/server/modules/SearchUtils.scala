package org.mulesoft.language.server.modules

import org.mulesoft.language.common.dtoTypes.{ILocation, IRange}

import org.mulesoft.high.level.Search;
import org.mulesoft.high.level.interfaces.IProject;
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

private class TextIssue(var label: String, var start: Int, var end: Int);

object SearchUtils {
	private var breakers = Seq(",", ".", ";", ":", " ", "\"", "'","{", "}", "[", "]", "/", "|", "\n", "\t", "\r").map(_.charAt(0));
	
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
	
	def findReferences(project: IProject, position: Int): Option[Seq[ILocation]] = {
		Search.findReferencesByPosition(project.rootASTUnit, position) match {
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
				
				Some(result.sortWith((l1, l2) => l1.range.start < l2.range.start));
			};
			
			case _ => None;
		}
	}
	
	def findDeclaration(project: IProject, position: Int): Option[Seq[ILocation]] = {
		Search.findDefinitionByPosition(project.rootASTUnit, position) match {
			case Some(searchResult) => Some(Seq(searchResult.definition).map(_.sourceInfo.ranges.headOption).filter(_ match {
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
				}
			}));
			
			case _ => None;
		}
	}
    // $COVERAGE-OFF$
	def findAll(project: IProject, position: Int): Option[Seq[ILocation]] = {
		(findDeclaration(project, position) match {
			case Some(result) => if(result.isEmpty) {
				None;
			} else {
				Some(result)
			}

			case _ => None
		}) match {
			case Some(result) => findReferences(project, result.head.range.start + 1) match {
				case Some(refs) => Some(refs.toBuffer += result.head);

				case _ => None
			};

			case _ => findReferences(project, position) match {
				case Some(refs) => Some(refs.toBuffer += new ILocation {
					var issue = findTextIssue(project.rootASTUnit.text, position);

					var range: IRange = new IRange(issue.start, issue.end);

					var uri: String = project.rootASTUnit.path.replace("file:///", "/");

					var version: Int = -1;
				});

				case _ => None
			};
		}
	}
    // $COVERAGE-OFF$
	//Some(findReferences(project, result.head.range.start + 1).get.toBuffer += result.head);
}
