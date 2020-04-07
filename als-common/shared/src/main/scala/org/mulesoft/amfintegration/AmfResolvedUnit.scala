package org.mulesoft.amfintegration

import amf.core.model.document.BaseUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class AmfResolvedUnit(val resolvedUnit: BaseUnit) {
  val originalUnit: BaseUnit

  protected def nextIfNotLast(): Option[Future[AmfResolvedUnit]]

  private def getLastRecursively(r: AmfResolvedUnit): Future[AmfResolvedUnit] =
    r.nextIfNotLast() match {
      case Some(f) => f.flatMap(a => getLastRecursively(a))
      case None    => Future.successful(r)
    }

  def latestBU: Future[BaseUnit] =
    getLastRecursively(this).map(_.resolvedUnit)
}
