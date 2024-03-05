package org.mulesoft.als.server.acv

import amf.custom.validation.client.scala.report.model._
import org.eclipse.lsp4j.{Location, Position, Range}
import org.mulesoft.als.server.modules.diagnostic.custom.TraceValueParser

import scala.collection.JavaConverters._
import scala.language.{implicitConversions, postfixOps}

object CustomDiagnosticReportBuilder {
  def toDiagnosticReport(opaReport: AMLOpaReport, apiUri: String): CustomDiagnosticReport =
    new CustomDiagnosticReport(
      opaReport.profileName.profile,
      opaReport.results.map(toDiagnosticEntry(_, apiUri)).toList.asJava
    )
  def toDiagnosticEntry(opaResult: AMLOpaResult, apiUri: String): CustomDiagnosticEntry =
    new CustomDiagnosticEntry(
      opaResult.level,
      opaResult.validationId.orNull,
      opaResult.validationName.orNull,
      opaResult.message,
      opaResult.location.map(toLocation).orNull,
      opaResult.trace.flatMap(toTrace(_, apiUri)).toList.asJava
    )
  def toTrace(trace: OpaTrace, apiUri: String): Seq[TraceEntry] = trace.traceValue.map { tv =>
    val tvp = new TraceValueParser(tv, apiUri)
    new TraceEntry(
      tvp.buildMessage(None).getOrElse(""),
      tv.subResult.flatMap(_.trace).flatMap(toTrace(_, apiUri)).toList.asJava,
      trace.location.map(toLocation).orNull
    )
  }
  def toLocation(location: OpaLocation): Location =
    new Location(location.location.getOrElse(""), location.range.map(toRange).getOrElse(rangeZero))
  def toRange(range: OpaRange): Range             = new Range(toPosition(range.start), toPosition(range.end))
  def toPosition(position: OpaPosition): Position = new Position(position.line, position.column)
  val positionZero                                = new Position(0, 0)
  val rangeZero                                   = new Range(positionZero, positionZero)
}
