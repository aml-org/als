package org.mulesoft.amfintegration

import amf.core.client.scala.AMFResult
import amf.core.client.scala.errorhandling.{AMFErrorHandler, DefaultErrorHandler}
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  AMLSpecificConfiguration,
  ProjectConfigurationState
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AmfResolvedUnit extends UnitWithNextReference {
  override protected type T = AmfResolvedUnit
  val alsConfigurationState: ALSConfigurationState
  val configuration: AMLSpecificConfiguration = AMLSpecificConfiguration(alsConfigurationState.getAmfConfig)

  protected def resolvedUnitFn(): Future[AMFResult]

  val diagnosticsBundle: Map[String, DiagnosticsBundle]

  val eh: AMFErrorHandler = DefaultErrorHandler()
  val baseUnit: BaseUnit

  final lazy val resolvedUnit: Future[AMFResult] = resolvedUnitFn()

  private def getLastRecursively(r: AmfResolvedUnit): Future[AmfResolvedUnit] =
    r.next match {
      case Some(f) => f.flatMap(a => getLastRecursively(a))
      case None    => Future.successful(r)
    }

  final def latestBU: Future[BaseUnit] =
    getLastRecursively(this).flatMap(_.resolvedUnit).map(_.baseUnit)
}
