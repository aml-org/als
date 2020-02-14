package org.mulesoft.als.server.modules.diagnostic

import amf.core.annotations.LexicalInformation
import amf.core.validation.AMFValidationResult
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.workspace.DiagnosticsBundle
import org.mulesoft.lsp.feature.common.Location
import org.mulesoft.lsp.feature.diagnostic.DiagnosticRelatedInformation

object DiagnosticConverters {
  def buildIssueResults(results: Map[String, Seq[AMFValidationResult]],
                        references: Map[String, DiagnosticsBundle]): Seq[ValidationReport] = {

    val issuesWithStack = buildIssues(results.values.flatten.toSeq, references)
    results
      .map(t => ValidationReport(t._1, issuesWithStack.filter(_.filePath == t._1).toSet))
      .toSeq
      .sortBy(_.pointOfViewUri)
  }

  private def buildIssues(results: Seq[AMFValidationResult],
                          references: Map[String, DiagnosticsBundle]): Seq[ValidationIssue] = {
    results.flatMap { r =>
      references.get(r.location.getOrElse("")) match {
        case Some(t)
            if !t.isExternal && t.references.nonEmpty => // Has stack, ain't ExternalFragment todo: check if it's a syntax error?
          t.references.map { stackContainer =>
            buildIssue(
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
            s"from ${r.location.getOrElse("")} ${range}"
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
                rootAsRelatedInfo
            )
          }
        case _ =>
          Seq(buildIssue(r, Nil))
      }
    }
  }

  private def buildIssue(r: AMFValidationResult, stack: Seq[DiagnosticRelatedInformation]): ValidationIssue = {
    ValidationIssue("PROPERTY_UNUSED",
                    ValidationSeverity(r.level),
                    r.location.getOrElse(""),
                    r.message,
                    lexicalToPosition(r.position),
                    stack)
  }

  private def buildIssue(path: String,
                         range: PositionRange,
                         message: String,
                         level: String,
                         stack: Seq[DiagnosticRelatedInformation]): ValidationIssue = {
    ValidationIssue("PROPERTY_UNUSED", ValidationSeverity(level), path, message, range, stack)
  }

  private def lexicalToPosition(maybeLi: Option[LexicalInformation]): PositionRange =
    maybeLi.map(position => PositionRange(position.range)).getOrElse(PositionRange(Position(0, 0), Position(0, 0)))

}
