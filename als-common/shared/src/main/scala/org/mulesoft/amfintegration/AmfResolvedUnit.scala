package org.mulesoft.amfintegration

import amf.core.client.scala.errorhandling.{AMFErrorHandler, DefaultErrorHandler}
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AmfResolvedUnit extends UnitWithNextReference {
  override protected type T = AmfResolvedUnit
  val amfConfiguration: AmfConfigurationWrapper

  protected def resolvedUnitFn(): Future[BaseUnit]

  val diagnosticsBundle: Map[String, DiagnosticsBundle]

  val eh: AMFErrorHandler = DefaultErrorHandler()
  val baseUnit: BaseUnit

  final lazy val resolvedUnit: Future[BaseUnit] = resolvedUnitFn()

  private def getLastRecursively(r: AmfResolvedUnit): Future[AmfResolvedUnit] =
    r.next match {
      case Some(f) => f.flatMap(a => getLastRecursively(a))
      case None    => Future.successful(r)
    }

  final def latestBU: Future[BaseUnit] =
    getLastRecursively(this).flatMap(_.resolvedUnit)
}