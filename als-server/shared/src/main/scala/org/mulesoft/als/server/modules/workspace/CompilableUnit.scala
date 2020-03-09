package org.mulesoft.als.server.modules.workspace

import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.ReferenceStack

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class CompilableUnit(uri: String,
                          unit: BaseUnit,
                          mainFile: Option[String],
                          next: Option[Future[CompilableUnit]],
                          stack: Seq[ReferenceStack],
                          isDirty: Boolean = false) {

  def getLast: Future[CompilableUnit] = {
    next match {
      case Some(f) => f.flatMap(cu => cu.getLast)
      case _       => Future.successful(this)
    }
  }
}
