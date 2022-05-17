package org.mulesoft.als.server.modules.workspace

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.common.dtoTypes.ReferenceStack
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.ErrorsCollected
import org.mulesoft.amfintegration.amfconfiguration.{AmfParseContext, AmfParseResult}

import scala.concurrent.Future

case class ParsedUnit(parsedResult: AmfParseResult, inTree: Boolean, definedBy: Dialect) {
  def toCU(
      next: Option[Future[CompilableUnit]],
      mf: Option[String],
      stack: Seq[ReferenceStack],
      isDirty: Boolean = false,
      amfContext: AmfParseContext
  ): CompilableUnit =
    CompilableUnit(
      parsedResult.result.baseUnit.identifier,
      parsedResult.result.baseUnit,
      if (inTree) mf else None,
      stack,
      isDirty,
      next,
      definedBy,
      ErrorsCollected(parsedResult.result.results.toList),
      amfContext
    )
}
