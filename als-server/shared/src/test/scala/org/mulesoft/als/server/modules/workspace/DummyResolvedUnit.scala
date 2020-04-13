package org.mulesoft.als.server.modules.workspace

import amf.client.parse.DefaultErrorHandler
import amf.core.model.document.BaseUnit
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.amfintegration.AmfResolvedUnit

import scala.concurrent.Future

trait DummyResolvedUnit {

  def dummyResolved(amfBaseUnit: BaseUnit, container: Option[TextDocumentContainer] = None): AmfResolvedUnit = {
    val cloned = amfBaseUnit.cloneUnit()

    new AmfResolvedUnit() {
      override val originalUnit: BaseUnit = amfBaseUnit

      override def nextIfNotLast(): Option[Future[AmfResolvedUnit]] = None

      override protected def resolvedUnitFn(): Future[BaseUnit] =
        Future.successful(
          container
            .map(_.amfConfiguration.parserHelper.editingResolve(cloned, DefaultErrorHandler()))
            .getOrElse(cloned))
    }
  }
}
