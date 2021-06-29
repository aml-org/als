package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.cache.UnitWithCaches
import org.mulesoft.als.common.dtoTypes.ReferenceStack
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.amfintegration.{ErrorsCollected, UnitWithNextReference}

import scala.concurrent.Future

case class CompilableUnit(uri: String,
                          unit: BaseUnit,
                          mainFile: Option[String],
                          stack: Seq[ReferenceStack],
                          isDirty: Boolean = false,
                          private val n: Option[Future[CompilableUnit]],
                          override val definedBy: Dialect,
                          errorsCollected: ErrorsCollected,
                          amfConfiguration: AmfConfigurationWrapper)
    extends UnitWithNextReference
    with UnitWithCaches {
  override protected type T = CompilableUnit
  override def next: Option[Future[T]] = n
}
