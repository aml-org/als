package org.mulesoft.amfintegration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait UnitWithNextReference {
  protected type T <: UnitWithNextReference
  def next: Option[Future[T]]
  def getLast: Future[T] = next match {
    case Some(f) =>
      f.flatMap(u => u.getLast).map(_.asInstanceOf[T])
    case _ =>
      Future.successful(this.asInstanceOf[T])
  }
}
