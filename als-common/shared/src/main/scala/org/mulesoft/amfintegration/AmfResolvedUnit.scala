package org.mulesoft.amfintegration

import amf.client.parse.DefaultErrorHandler
import amf.core.errorhandling.ErrorCollector
import amf.core.model.document.BaseUnit
import amf.plugins.document.webapi.model.{Extension, Overlay}
import org.mulesoft.als.configuration.UnitWithWorkspaceConfiguration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AmfResolvedUnit extends UnitWithNextReference with UnitWithWorkspaceConfiguration {
  override protected type T = AmfResolvedUnit

  protected def resolvedUnitFn(): Future[BaseUnit]

  val diagnosticsBundle: Map[String, DiagnosticsBundle]

  val eh: ErrorCollector = DefaultErrorHandler()
  val originalUnit: BaseUnit

  final lazy val resolvedUnit: Future[BaseUnit] = resolvedUnitFn()

  private def getLastRecursively(r: AmfResolvedUnit): Future[AmfResolvedUnit] =
    r.next match {
      case Some(f) => f.flatMap(a => getLastRecursively(a))
      case None    => Future.successful(r)
    }

  final def latestBU: Future[BaseUnit] =
    getLastRecursively(this).flatMap(_.resolvedUnit)

}
