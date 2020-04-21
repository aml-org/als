package org.mulesoft.als.server.modules.diagnostic

import amf.ProfileName
import amf.core.annotations.LexicalInformation
import amf.core.validation.AMFValidationResult
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.DiagnosticsBundle
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation
import org.mulesoft.lsp.feature.common.Range

object DiagnosticConverters {

  private val UNKNOWN_LOCATION = "Unknown location for error. Could not retrieve effective location"
  def buildIssueResults(results: Map[String, Seq[AMFValidationResult]],
                        references: Map[String, DiagnosticsBundle],
                        profile: ProfileName): Seq[ValidationReport] = {

    val issuesWithStack = buildIssues(results, references)
    results
      .map(t => ValidationReport(t._1, issuesWithStack.filter(_.filePath == t._1).toSet, profile))
      .toSeq
      .sortBy(_.pointOfViewUri)
  }

  // need to search with path because location of result could be empty?
  private def buildLocatedIssue(uri: String,
                                results: Seq[AMFValidationResult],
                                references: Map[String, DiagnosticsBundle]) = {
    results.flatMap { r =>
      references.get(uri) match {
        case Some(t)
            if !t.isExternal && t.references.nonEmpty => // Has stack, ain't ExternalFragment todo: check if it's a syntax error?
          t.references.map { stackContainer =>
            buildIssue(
              uri,
              r,
              stackContainer.stack
                .map(
                  s =>
                    DiagnosticRelatedInformation(Location(s.originUri, LspRangeConverter.toLspRange(s.originRange)),
                                                 s"at ${s.originUri} ${s.originRange}"))
            )
          }
        case Some(t) if t.references.nonEmpty =>
          // invert order of stack, put root as last element of the trace
          val range = LspRangeConverter.toLspRange(
            r.position
              .map(position => PositionRange(position.range))
              .getOrElse(PositionRange(Position(0, 0), Position(0, 0))))
          val rootAsRelatedInfo: DiagnosticRelatedInformation = DiagnosticRelatedInformation(
            Location(
              r.location.getOrElse(""),
              range
            ),
            s"from ${r.location.getOrElse("")} $range"
          )

          t.references.map { stackContainer =>
            val newHead = stackContainer.stack.last

            buildIssue(
              newHead.originUri,
              newHead.originRange,
              r.message,
              r.level,
              stackContainer.stack.reverse
                .drop(1)
                .map(s =>
                  DiagnosticRelatedInformation(Location(s.originUri, LspRangeConverter.toLspRange(s.originRange)),
                                               s"from ${s.originUri}")) :+
                rootAsRelatedInfo,
              r.validationId
            )
          }
        case _ =>
          Seq(buildIssue(uri, r, Nil))
      }
    }
  }
  private def buildIssues(results: Map[String, Seq[AMFValidationResult]],
                          references: Map[String, DiagnosticsBundle]): Seq[ValidationIssue] = {
    results.flatMap { case (uri, r) => buildLocatedIssue(uri, r, references) }.toSeq
  }
  private def buildIssue(iri: String,
                         r: AMFValidationResult,
                         stack: Seq[DiagnosticRelatedInformation]): ValidationIssue = {
    val position = lexicalToPosition(r.position)
    ValidationIssue(r.validationId,
                    ValidationSeverity(r.level),
                    r.location.getOrElse(iri),
                    r.message,
                    position,
                    stack ++ buildUnknowLocation(iri, r, position))
  }

  private def buildUnknowLocation(iri: String,
                                  r: AMFValidationResult,
                                  positionRange: PositionRange): Option[DiagnosticRelatedInformation] = {
    r.location match {
      case Some(_) => None
      case None    => Some(buildDiagnosticRelated(iri, LspRangeConverter.toLspRange(positionRange)))
    }
  }
  private def buildDiagnosticRelated(iri: String, range: Range) =
    DiagnosticRelatedInformation(Location(iri, range), UNKNOWN_LOCATION)
  private def buildIssue(path: String,
                         range: PositionRange,
                         message: String,
                         level: String,
                         stack: Seq[DiagnosticRelatedInformation],
                         validationId: String): ValidationIssue = {
    ValidationIssue(validationId, ValidationSeverity(level), path, message, range, stack)
  }

  private def lexicalToPosition(maybeLi: Option[LexicalInformation]): PositionRange =
    maybeLi.map(position => PositionRange(position.range)).getOrElse(PositionRange(Position(0, 0), Position(0, 0)))

}
