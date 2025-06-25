package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.cache.UnitWithCaches
import org.mulesoft.amfintegration.amfconfiguration.{AmfParseContext, DocumentDefinition}
import org.mulesoft.amfintegration.{ErrorsCollected, UnitWithNextReference}

import scala.concurrent.Future

case class CompilableUnit(
                           uri: String,
                           unit: BaseUnit,
                           mainFile: Option[String],
                           isDirty: Boolean = false,
                           private val n: Option[Future[CompilableUnit]],
                           override val documentDefinition: DocumentDefinition,
                           errorsCollected: ErrorsCollected,
                           context: AmfParseContext
) extends UnitWithNextReference
    with UnitWithCaches {
  override protected type T = CompilableUnit
  override def next: Option[Future[T]] = n
}
