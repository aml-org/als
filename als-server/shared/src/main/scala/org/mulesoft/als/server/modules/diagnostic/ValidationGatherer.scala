package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.collection.mutable
import scala.scalajs.js.annotation.JSExport

class ValidationGatherer(telemetryProvider: TelemetryProvider) {

  private val resultsByUnit: mutable.Map[DiagnosticManagerKind, mutable.Map[String, Seq[AlsValidationResult]]] =
    mutable.Map.empty

  private def merge(
      left: Map[String, Seq[AlsValidationResult]],
      right: Map[String, Seq[AlsValidationResult]]
  ): Map[String, Seq[AlsValidationResult]] =
    left.map { case (k, v) =>
      k -> (v ++ right.getOrElse(k, Nil))
    } ++ right.filter(t => !left.keys.exists(_ == t._1))

  def merged(): Map[String, Seq[AlsValidationResult]] =
    resultsByUnit.mapValues(_.toMap).values.reduce { merge }

  def removeFile(uri: String, manager: DiagnosticManagerKind): Unit =
    resultsByUnit.get(manager).foreach(_.remove(uri))

  def indexNewReport(result: ErrorsWithTree, manager: DiagnosticManagerKind, uuid: String): Unit = synchronized {
    if (!resultsByUnit.contains(manager))
      resultsByUnit.update(manager, mutable.Map.empty)
    val results: Map[String, Seq[AlsValidationResult]] =
      result.errors.groupBy(r => r.result.location.getOrElse(result.location))

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

case class ErrorsWithTree(location: String, errors: Seq[AlsValidationResult], tree: Option[Set[String]])

object ErrorsWithTree {
  def apply(location: String, errors: Seq[AlsValidationResult], tree: Option[Set[String]]) =
    new ErrorsWithTree(location, errors, tree)
}

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

object CustomDiagnosticKind extends DiagnosticManagerKind {
  override val name: String = "custom"
}

object ProjectDiagnosticKind extends DiagnosticManagerKind {
  override val name: String = "project"
}
