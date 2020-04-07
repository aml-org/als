package org.mulesoft.als.server.modules.workspace

import amf.core.model.document.BaseUnit
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.amfintegration.AmfResolvedUnit

import scala.concurrent.Future

trait DummyResolvedUnit {

  def dummyResolved(amfBaseUnit: BaseUnit, container: Option[TextDocumentContainer] = None): AmfResolvedUnit = {
    val cloned = amfBaseUnit.cloneUnit()
    val resolved =
      container.map(_.amfConfiguration.parserHelper.editingResolve(cloned)).getOrElse(cloned)

    new AmfResolvedUnit(resolved) {
      override val originalUnit: BaseUnit = amfBaseUnit

      override def nextIfNotLast(): Option[Future[AmfResolvedUnit]] = None
    }
  }
}