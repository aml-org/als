package org.mulesoft.als.server.acv

import amf.custom.validation.client.scala.report.model.{
  AMLLocation,
  AMLOpaReport,
  AMLOpaResult,
  AMLPosition,
  AMLRange,
  OpaTrace
}
import org.eclipse.lsp4j.{Location, Position, Range}
import org.mulesoft.als.server.modules.diagnostic.custom.ResultParser

import scala.collection.JavaConverters._
import scala.language.{implicitConversions, postfixOps}
object CustomDiagnosticReportBuilder {
  def toDiagnosticReport(opaReport: AMLOpaReport): CustomDiagnosticReport = {

    val parsedResult = new ResultParser(opaResult = ???, rootUri = ???)

    parsedResult.buildStack()
    new CustomDiagnosticReport(
      opaReport.profileName.profile,
      opaReport.results.map(toDiagnosticEntry).toList.asJava
    )
  }
  def toDiagnosticEntry(opaResult: AMLOpaResult): CustomDiagnosticEntry =
    new CustomDiagnosticEntry(
      opaResult.level,
      opaResult.validationId.orNull,
      opaResult.validationName.orNull,
      opaResult.message,
      opaResult.location.map(toLocation).orNull,
      opaResult.trace.map(toTrace).toList.asJava
    )
  def toTrace(trace: OpaTrace): TraceEntry = new TraceEntry()
  private def toLocation(location: AMLLocation): Location =
    new Location(location.location.getOrElse(""), location.range.map(toRange).getOrElse(rangeZero))
  private def toRange(range: AMLRange): Range             = new Range(toPosition(range.start), toPosition(range.end))
  private def toPosition(position: AMLPosition): Position = new Position(position.line, position.column)
  private val positionZero                                = new Position(0, 0)
  private val rangeZero                                   = new Range(positionZero, positionZero)
}
