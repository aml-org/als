package org.mulesoft.als.suggestions

import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.CompletionRequest

class AMLCompletionParams(private val request: CompletionRequest, val prefix: String) {

  val baseUnit: BaseUnit                     = request.baseUnit
  val propertyMappings: Seq[PropertyMapping] = request.propertyMapping
  val position: Position                     = request.position
  val amfObject: AmfObject                   = request.amfObject
  val dialect: Dialect                       = request.actualDialect
  val yPartBranch: YPartBranch               = request.yPartBranch
  val fieldEntry: Option[FieldEntry]         = request.fieldEntry
  lazy val declarationProvider: DeclarationProvider =
    DeclarationProvider(baseUnit, Some(dialect))
}

object RequestToCompletionParams {
  implicit class RequestConverter(request: CompletionRequest) {
    def toParams(linePrefix: String): AMLCompletionParams = new AMLCompletionParams(request, linePrefix)
  }
}
