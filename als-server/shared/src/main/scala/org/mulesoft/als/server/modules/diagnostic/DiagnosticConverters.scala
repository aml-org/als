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

object DiagnosticConverters {

  private val UNKNOWN_LOCATION = "Unknown location for error. Could not retrieve effective location"
  def buildIssueResults(
      results: Map[String, Seq[AlsValidationResult]],
      references: Map[String, Seq[DocumentLink]],
      profile: ProfileName
  ): Seq[ValidationReport] = {

    val issuesWithStack = buildIssues(results, references)
    val r = results
      .map(t => ValidationReport(t._1, issuesWithStack.filter(_.filePath == t._1).toSet, profile))
      .toSeq
      .sortBy(_.pointOfViewUri)
    r
  }

  private val syntaxViolationIds = Seq(CoreValidations.SyamlError.id, CoreValidations.SyamlWarning.id)
  // need to search with path because location of result could be empty?
  private def isExternalAndNotSyntax(r: AMFValidationResult) = {
    syntaxViolationIds.contains(r.validationId) // &&  is external fragment?
  }

  private def relatedFor(uri: String, references: Seq[(DocumentLink, String)]): Seq[DiagnosticRelatedInformation] = {
    val calls = references.filter { case (DocumentLink(_, target, _), _) =>
      target == uri
    }
    calls.flatMap { case (DocumentLink(range, _, _), origin) =>
      DiagnosticRelatedInformation(Location(origin, range), s"at $uri") +: relatedFor(origin, references)
    }
  }

  private def buildLocatedIssue(
      uri: String,
      results: Seq[AlsValidationResult],
      references: Map[String, Seq[DocumentLink]]
  ): Seq[ValidationIssue] = {
    results.map { s =>
      val r = s.result
      val reversedReferences: Seq[(DocumentLink, String)] =
        references.toSeq.flatMap { case (uri, links) =>
          links.map(l => (l, uri))
        }
      buildIssue(uri, r, relatedFor(uri, reversedReferences))
//      references.get(uri) match {
//        case Some(DocumentLink(range, target, _)) if isExternalAndNotSyntax(r) =>
//          DiagnosticRelatedInformation(Location(uri, range), s"at $uri") +:
//
//          t.references.map { stackContainer =>
//            buildIssue(
//              uri,
//              r,
//              s.stack ++ stackContainer.stack
//                .map(s =>
//                  DiagnosticRelatedInformation(
//                    Location(s.originUri, LspRangeConverter.toLspRange(s.originRange)),
//                    s"at ${s.originUri}"
//                  )
//                )
//            )
//          }
//        case Some(t) if t.references.nonEmpty && t.references.exists(_.stack.nonEmpty) =>
//          // invert order of stack, put root as last element of the trace
//          val range = LspRangeConverter.toLspRange(
//            r.position
//              .map(position => PositionRange(position.range))
//              .getOrElse(PositionRange(Position(0, 0), Position(0, 0)))
//          )
//          val rootAsRelatedInfo: DiagnosticRelatedInformation = DiagnosticRelatedInformation(
//            Location(
//              r.location.getOrElse(""),
//              range
//            ),
//            s"from ${r.location.getOrElse("")}"
//          )
//
//          t.references
//            .flatMap(stackContainer => stackContainer.stack.lastOption.map(newHead => (newHead, stackContainer)))
//            .map { x =>
//              val (newHead, stackContainer) = x
//              buildIssue(
//                newHead.originUri,
//                newHead.originRange,
//                r.message,
//                r.severityLevel,
//                s.stack ++ stackContainer.stack.reverse
//                  .drop(1)
//                  .map(s =>
//                    DiagnosticRelatedInformation(
//                      Location(s.originUri, LspRangeConverter.toLspRange(s.originRange)),
//                      s"from ${s.originUri}"
//                    )
//                  ) :+
//                  rootAsRelatedInfo,
//                r.validationId
//              )
//            }
//        case _ =>
//          Seq(buildIssue(uri, r, s.stack))
//      }
    }
  }
  private def buildIssues(
      results: Map[String, Seq[AlsValidationResult]],
      references: Map[String, Seq[DocumentLink]]
  ): Seq[ValidationIssue] = {
    results.flatMap { case (uri, r) => buildLocatedIssue(uri, r, references) }.toSeq
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
