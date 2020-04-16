package org.mulesoft.amfintegration

import amf.client.parse.DefaultErrorHandler
import amf.core.errorhandling.ErrorCollector
import amf.core.model.document.BaseUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AmfResolvedUnit {
  protected def resolvedUnitFn(): Future[BaseUnit]

  val eh: ErrorCollector = DefaultErrorHandler()

  val originalUnit: BaseUnit
  final lazy val resolvedUnit: Future[BaseUnit] = resolvedUnitFn()

  protected def nextIfNotLast(): Option[Future[AmfResolvedUnit]]

  private def getLastRecursively(r: AmfResolvedUnit): Future[AmfResolvedUnit] =
    r.nextIfNotLast() match {
      case Some(f) => f.flatMap(a => getLastRecursively(a))
      case None    => Future.successful(r)
    }

  final def latestBU: Future[BaseUnit] =
    getLastRecursively(this).flatMap(_.resolvedUnit)
}
