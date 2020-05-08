package org.mulesoft.als.server.modules.workspace

import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.ReferenceStack
import org.mulesoft.amfmanager.AmfImplicits._

import scala.concurrent.Future

case class ParsedUnit(bu: BaseUnit, inTree: Boolean) {
  def toCU(next: Option[Future[CompilableUnit]],
           mf: Option[String],
           stack: Seq[ReferenceStack],
           isDirty: Boolean = false): CompilableUnit =
    CompilableUnit(bu.identifier, bu, if (inTree) mf else None, stack, isDirty, next)
}