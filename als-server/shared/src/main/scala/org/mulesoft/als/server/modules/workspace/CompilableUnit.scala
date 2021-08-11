package org.mulesoft.als.server.modules.workspace

import amf.core.errorhandling.ErrorCollector
import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.cache.UnitWithCaches
import org.mulesoft.als.common.dtoTypes.ReferenceStack
import org.mulesoft.als.configuration.{UnitWithWorkspaceConfiguration, WorkspaceConfiguration}
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
                          override val workspaceConfiguration: Option[WorkspaceConfiguration])
    extends UnitWithNextReference
    with UnitWithWorkspaceConfiguration
    with UnitWithCaches {
  override protected type T = CompilableUnit
  override def next: Option[Future[T]] = n
}
