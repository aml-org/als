package org.mulesoft.language.server.modules

import common.dtoTypes.{Position, PositionRange}
import org.mulesoft.high.level.Search
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.ILocation

import scala.collection.mutable.ListBuffer

private case class TextIssue(label: String, start: Int, end: Int)

object SearchUtils {
  private val breakers =
    Seq(",", ".", ";", ":", " ", "\"", "'", "{", "}", "[", "]", "/", "|", "\n", "\t", "\r").map(_.charAt(0))

  private def findTextIssue(content: String, position: Int): TextIssue = {
    var start = position
    var end = position

    if (isBreaker(end, content)) {
      end = end - 1
    }

    if (isBreaker(start, content)) {
      start = end
    }

    while (start > 0 && !isBreaker(start, content)) {
      start = start - 1
    }

    while (end < content.length && !isBreaker(end, content)) {
      end = end + 1
    }

    start = start + 1

    TextIssue(content.substring(start, end), start, end)
  }

  private def findTextIssues(content: String, label: String, searchStart: Int, searchEnd: Int): Seq[TextIssue] = {
    var issues: ListBuffer[TextIssue] = ListBuffer()

    var index = content.indexOf(label, searchStart)

    while (index > -1 && index < searchEnd) {
      issues += findTextIssue(content, index)

      index = content.indexOf(label, index + 1)
    }

    issues.filter(issue => issue.label == label).distinct
  }

  private def isBreaker(index: Int, content: String): Boolean = breakers.contains(content.charAt(index))

  def findReferences(project: IProject, position: Position): Option[Seq[ILocation]] =
    findReferences(project, position.offset(project.rootASTUnit.text))

  def findReferences(project: IProject, position: Int): Option[Seq[ILocation]] = {
    Search.findReferencesByPosition(project.rootASTUnit, position) match {
      case Some(searchResult) => {
        var result: ListBuffer[ILocation] = ListBuffer()

        val nodes = searchResult.references.filter(_.sourceInfo.ranges.headOption match {
          case Some(range) => range.start.resolved

          case _ => false
        })

        val issueName = findTextIssue(project.rootASTUnit.text, position).label

        nodes.foreach(node => {
          val nodeRange = node.sourceInfo.ranges.headOption.get

          findTextIssues(node.astUnit.text, issueName, nodeRange.start.position, nodeRange.end.position)
            .foreach(issue =>
              result += new ILocation {
                private val rawText: String = node.astUnit.text

                var posRange: PositionRange =
                  PositionRange(Position(issue.start, rawText), Position(issue.end, rawText))

                var uri: String = node.astUnit.path.replace("file:///", "/") //TODO: keep protocol?

                var version: Int = -1

                override def toString: String = uri + "_" + issue.start + "_" + issue.end

                override def equals(obj: scala.Any): Boolean = toString().equals(obj.toString())

                override def hashCode(): Int = toString().hashCode()
              })
        })

        Some(result.sortWith((l1, l2) => l1.posRange.start < l2.posRange.start))
      }

      case _ => None;
    }
  }

  def findDeclaration(project: IProject, position: Position): Option[Seq[ILocation]] = {
    Search.findDefinitionByPosition(project.rootASTUnit, position) match {
      case Some(searchResult) =>
        Some(
          Seq(searchResult.definition)
            .map(_.sourceInfo.ranges.headOption)
            .filter(_ match {
              case Some(range) => range.start.resolved

              case _ => false
            })
            .map(lowLevelRange => {
              val unit = searchResult.definition.astUnit

              val start = lowLevelRange.get.start.position

              val end = start + unit.text.substring(start).indexOf(":")

              new ILocation {
                private val rawText: String = unit.text

                var posRange: PositionRange = PositionRange(Position(start, rawText), Position(end, rawText))

                var uri: String = unit.path.replace("file:///", "/")

                var version: Int = -1
              }
            }))

      case _ => None
    }
  }

  def findAll(project: IProject, position: Position): Option[Seq[ILocation]] = {
    (findDeclaration(project, position) match {
      case Some(result) =>
        if (result.isEmpty) {
          None
        } else {
          Some(result)
        }

      case _ => None
    }) match {
      case Some(result) =>
        findReferences(project, result.head.posRange.start.offset(project.rootASTUnit.text) + 1) match {
          case Some(refs) => Some(refs.toBuffer += result.head)
          case _ => None
        }

      case _ =>
        findReferences(project, position) match {
          case Some(refs) =>
            Some(refs.toBuffer += new ILocation {
              private val text: String = project.rootASTUnit.text

              var issue: TextIssue = findTextIssue(text, position.offset(text))

              var posRange: PositionRange = PositionRange(Position(issue.start, text), Position(issue.end, text))

              var uri: String = project.rootASTUnit.path.replace("file:///", "/")

              var version: Int = -1
            })

          case _ => None
        }
    }
  }
}
