package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileName
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.annotations.LexicalInformation
import amf.core.internal.validation.CoreValidations
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.{Location, Range}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation
import org.mulesoft.lsp.feature.link.DocumentLink

import scala.collection.generic.SeqForwarder
import scala.collection.mutable.ListBuffer
import scala.collection.{AbstractSeq, LinearSeq, SeqProxy, SeqViewLike, immutable, mutable}

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

  private def relatedFor(uri: String, references: Seq[(DocumentLink, String)]): Seq[DiagnosticRelatedInformation] = {
    val calls = references.filter { case (DocumentLink(_, target, _), _) =>
      target == uri
    }
    calls.flatMap { case (DocumentLink(range, _, _), origin) =>
      DiagnosticRelatedInformation(Location(origin, range), s"at $uri") +: relatedFor(origin, references)
    }
  }

  private def relatedForOrigin(
      uri: String,
      references: Seq[(DocumentLink, String)],
      informationBranches: mutable.ListBuffer[mutable.ListBuffer[DiagnosticRelatedInformation]],
      branch: mutable.ListBuffer[DiagnosticRelatedInformation]
  ): Unit = {
    filterReferences(uri, references) match {
      case head :: tail =>
        head match {
          case (DocumentLink(range, _, _), origin) =>
            branch.append(newDiagnostic(uri, range, origin))
            relatedForOrigin(origin, references, informationBranches, branch)
        }
        tail.foreach { case (DocumentLink(range, _, _), origin) =>
          val newBranch: mutable.ListBuffer[DiagnosticRelatedInformation] = branch.clone()
          newBranch.append(newDiagnostic(uri, range, origin))
          informationBranches.append(newBranch)
          relatedForOrigin(origin, references, informationBranches, newBranch)
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
      val r = s.result
      val reversedReferences: Seq[(DocumentLink, String)] =
        references.toSeq.flatMap { case (uri, links) =>
          links.map(l => (l, uri))
        }
      if (notExternalOrSyntax(r, isExternal, references.getOrElse(uri, Seq()))) {
        Seq(buildIssue(uri, r, relatedFor(uri, reversedReferences)))
      } else {
        getInformationStackBranches(uri, reversedReferences).map { informationStack =>
          val range = LspRangeConverter.toLspRange(
            r.position
              .map(position => PositionRange(position.range))
              .getOrElse(PositionRange(Position(0, 0), Position(0, 0)))
          )

          val newDiag = newDiagnostic(
            {
              informationStack.last.location.uri
            },
            range,
            r.location.getOrElse(uri)
          )
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
      }
    }
  }

  private def getInformationStackBranches(
      uri: String,
      reversedReferences: Seq[(DocumentLink, String)]
  ): Seq[Seq[DiagnosticRelatedInformation]] = {
    val informationBranches: ListBuffer[ListBuffer[DiagnosticRelatedInformation]] = mutable.ListBuffer()
    val mainBranch: ListBuffer[DiagnosticRelatedInformation]                      = mutable.ListBuffer()
    informationBranches.append(mainBranch)
    relatedForOrigin(uri, reversedReferences, informationBranches, mainBranch)
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
      stack ++ buildUnknowLocation(iri, r, position)
    )
  }

  private def buildUnknowLocation(
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
