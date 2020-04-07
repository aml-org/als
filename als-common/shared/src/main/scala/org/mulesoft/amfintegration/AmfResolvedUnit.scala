package org.mulesoft.amfintegration

import amf.core.model.document.BaseUnit

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class AmfResolvedUnit(val resolvedUnit: BaseUnit) {
  val originalUnit: BaseUnit

  protected def next(): Option[Future[AmfResolvedUnit]]

  def latestBU: Future[BaseUnit] = {
    def innerGetLast(r: AmfResolvedUnit): Future[AmfResolvedUnit] = {
      r.next()
        .map(_.flatMap { a: AmfResolvedUnit =>
          innerGetLast(a)
        })
        .getOrElse(Future.successful(r))
    }
    innerGetLast(this).map(_.resolvedUnit)
  }
}
