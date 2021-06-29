package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.validation.AMFValidationResult
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.collection.mutable
import scala.scalajs.js.annotation.JSExport

class ValidationGatherer(telemetryProvider: TelemetryProvider) {

  private val resultsByUnit: mutable.Map[DiagnosticManagerKind, mutable.Map[String, Seq[AMFValidationResult]]] =
    mutable.Map.empty

  private def merge(left: Map[String, Seq[AMFValidationResult]],
                    right: Map[String, Seq[AMFValidationResult]]): Map[String, Seq[AMFValidationResult]] =
    left.map {
      case (k, v) => k -> (v ++ right.getOrElse(k, Nil))
    } ++ right.filter(t => !left.keys.exists(_ == t._1))

  def merged(): Map[String, Seq[AMFValidationResult]] =
    resultsByUnit.mapValues(_.toMap).values.reduce { merge }

  def removeFile(uri: String, manager: DiagnosticManagerKind): Unit =
    resultsByUnit.get(manager).foreach(_.remove(uri))

  def indexNewReport(result: ErrorsWithTree, manager: DiagnosticManagerKind, uuid: String): Unit = synchronized {
    if (!resultsByUnit.contains(manager))
      resultsByUnit.update(manager, mutable.Map.empty)
    val results: Map[String, Seq[AMFValidationResult]] =
      result.errors.groupBy(r => r.location.getOrElse(result.location))

    result.tree.getOrElse(Set.empty).foreach { t =>
      results.get(t) match {
        case Some(r) =>
          resultsByUnit
            .get(manager)
            .foreach(_.update(t, r))
        case _ =>
          resultsByUnit.get(manager).foreach { _.update(t, Seq.empty) }
      }
    }
  }
}

case class DiagnosticNotificationsKind(kind: String)

case class ErrorsWithTree(location: String, errors: Seq[AMFValidationResult], tree: Option[Set[String]])

object PARSING_BEFORE extends DiagnosticNotificationsKind("PARSING_BEFORE")
object ALL_TOGETHER   extends DiagnosticNotificationsKind("ALL_TOGETHER")

@JSExport
object DiagnosticNotificationsKind {
  val parsingBefore: DiagnosticNotificationsKind = PARSING_BEFORE
  val allTogether: DiagnosticNotificationsKind   = ALL_TOGETHER
}

sealed trait DiagnosticManagerKind {
  val name: String
}

object ParserDiagnosticKind extends DiagnosticManagerKind {
  override val name: String = "parser"
}

object ResolutionDiagnosticKind extends DiagnosticManagerKind {
  override val name: String = "resolution"
}