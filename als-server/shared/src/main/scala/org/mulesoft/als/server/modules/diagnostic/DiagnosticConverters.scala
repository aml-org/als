package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileName
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.annotations.LexicalInformation
import amf.core.internal.validation.CoreValidations
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.{Location, Range}
import org.mulesoft.exceptions.PathTweaks
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object DiagnosticConverters {

  private val UNKNOWN_LOCATION = "Unknown location for error. Could not retrieve effective location"

  def buildIssueResults(
      results: Map[String, Seq[AlsValidationResult]],
      references: Map[String, Seq[DocumentLink]],
      profile: ProfileName,
      externalMaps: Map[String, Boolean] = Map()
  ): Seq[ValidationReport] = {
    val issuesWithStack = buildIssues(results, references, externalMaps)
    val r = results
      .map(t => ValidationReport(t._1, issuesWithStack.filter(_.filePath == t._1).toSet, profile))
      .toSeq
      .sortBy(_.pointOfViewUri)
    r
  }

  private val syntaxViolationIds = Seq(CoreValidations.SyamlError.id, CoreValidations.SyamlWarning.id)

  private def notExternalOrSyntax(
      r: AMFValidationResult,
      isExternal: Boolean,
      references: Seq[DocumentLink]
  ): Boolean = {
    syntaxViolationIds.contains(r.validationId) || (!isExternal && references.nonEmpty)
  }

  private def relatedFor(
      uri: String,
      references: Seq[(DocumentLink, String)],
      informationBranches: mutable.ListBuffer[mutable.ListBuffer[DiagnosticRelatedInformation]],
      branch: mutable.ListBuffer[DiagnosticRelatedInformation],
      branchLimit: Int,
      originFlag: Boolean
  ): Unit = {
    filterReferences(uri, references) match {
      case head :: tail =>
        if (informationBranches.size < branchLimit) {
          tail.foreach {
            case (DocumentLink(range, _, _), origin) if informationBranches.size < branchLimit =>
              val newBranch: mutable.ListBuffer[DiagnosticRelatedInformation] = branch.clone()
              if (originFlag)
                newBranch.prepend(newDiagnostic(uri, range, origin))
              else
                newBranch.append(newDiagnostic(uri, range, origin))
              informationBranches.append(newBranch)
              relatedFor(origin, references, informationBranches, newBranch, branchLimit, originFlag)
            case _ => // do nothing
          }
        }
        head match {
          case (DocumentLink(range, _, _), origin) =>
            val newD = newDiagnostic(uri, range, origin)
            // avoid timeout by checking if we already added it, not using set as order is needed
            if (!branch.contains(newD)) {
              if (originFlag)
                branch.prepend(newDiagnostic(uri, range, origin))
              else
                branch.append(newDiagnostic(uri, range, origin))
              relatedFor(origin, references, informationBranches, branch, branchLimit, originFlag)
            }
        }
      case _ => // over
    }
  }

  private def newDiagnostic(uri: String, range: Range, origin: String) =
    DiagnosticRelatedInformation(Location(origin, range), s"from $uri")

  private def filterReferences(uri: String, references: Seq[(DocumentLink, String)]): List[(DocumentLink, String)] =
    references.filter { case (DocumentLink(_, target, _), _) =>
      target == uri
    }.toList

  private def buildLocatedIssue(
      uri: String,
      results: Seq[AlsValidationResult],
      references: Map[String, Seq[DocumentLink]],
      isExternal: Boolean
  ): Seq[ValidationIssue] = {
    results.flatMap { s =>
      val tweakedUri: String = PathTweaks(uri)
      val r                  = s.result
      val reversedReferences: Seq[(DocumentLink, String)] =
        references.toSeq.flatMap { case (uri, links) =>
          links.map(l => (l, uri))
        }
      val reference = reversedReferences.filter(_._1.target.equalsIgnoreCase(tweakedUri)).map(_._1)
      if (notExternalOrSyntax(r, isExternal, reference)) {
        getInformationStackBranches(tweakedUri, reversedReferences, originFlag = false).map { informationStack =>
          buildIssue(tweakedUri, r, informationStack)
        }
      } else if (reversedReferences.exists(_._1.target.equalsIgnoreCase(tweakedUri))) {
        getInformationStackBranches(tweakedUri, reversedReferences, originFlag = true).map { informationStack =>
          val range = LspRangeConverter.toLspRange(
            r.position
              .map(position => PositionRange(position.range))
              .getOrElse(PositionRange(Position(0, 0), Position(0, 0)))
          )

          val newDiag = newDiagnostic(informationStack.last.location.uri, range, r.location.getOrElse(tweakedUri))
          val issue = buildIssue(
            informationStack.head.location.uri,
            PositionRange(informationStack.head.location.range),
            r.message,
            r.severityLevel,
            informationStack.tail :+ newDiag,
            r.validationId
          )
          issue
        }
      } else {
        Seq(buildIssue(tweakedUri, r, s.stack))
      }
    }
  }

  private def getInformationStackBranches(
      uri: String,
      reversedReferences: Seq[(DocumentLink, String)],
      originFlag: Boolean
  ): Seq[Seq[DiagnosticRelatedInformation]] = {
    val informationBranches: ListBuffer[ListBuffer[DiagnosticRelatedInformation]] = mutable.ListBuffer()
    val mainBranch: ListBuffer[DiagnosticRelatedInformation]                      = mutable.ListBuffer()
    val branchLimit                                                               = 100 // reduced to avoid sof
    informationBranches.append(mainBranch)
    relatedFor(uri, reversedReferences, informationBranches, mainBranch, branchLimit, originFlag)
    informationBranches.size
    informationBranches.map(_.toSeq)
  }

  private def buildIssues(
      results: Map[String, Seq[AlsValidationResult]],
      references: Map[String, Seq[DocumentLink]],
      externalMaps: Map[String, Boolean]
  ): Seq[ValidationIssue] = {
    results.flatMap { case (uri, r) => buildLocatedIssue(uri, r, references, externalMaps.getOrElse(uri, false)) }.toSeq
  }

  private def buildIssue(
      iri: String,
      r: AMFValidationResult,
      stack: Seq[DiagnosticRelatedInformation]
  ): ValidationIssue = {
    val position = lexicalToPosition(r.position)
    ValidationIssue(
      r.validationId,
      ValidationSeverity(r.severityLevel),
      r.location.getOrElse(iri),
      r.message,
      position,
      stack ++ buildUnknownLocation(iri, r, position)
    )
  }

  private def buildUnknownLocation(
      iri: String,
      r: AMFValidationResult,
      positionRange: PositionRange
  ): Option[DiagnosticRelatedInformation] = {
    r.location match {
      case Some(_) => None
      case None    => Some(buildDiagnosticRelated(iri, LspRangeConverter.toLspRange(positionRange)))
    }
  }

  private def buildDiagnosticRelated(iri: String, range: Range) =
    DiagnosticRelatedInformation(Location(iri, range), UNKNOWN_LOCATION)

  private def buildIssue(
      path: String,
      range: PositionRange,
      message: String,
      level: String,
      stack: Seq[DiagnosticRelatedInformation],
      validationId: String
  ): ValidationIssue = {
    ValidationIssue(validationId, ValidationSeverity(level), path, message, range, stack)
  }

  private def lexicalToPosition(maybeLi: Option[LexicalInformation]): PositionRange =
    maybeLi.map(position => PositionRange(position.range)).getOrElse(PositionRange(Position(0, 0), Position(0, 0)))

}
