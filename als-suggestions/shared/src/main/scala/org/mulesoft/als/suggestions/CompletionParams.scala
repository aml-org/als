package org.mulesoft.als.suggestions

import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.CompletionRequest

case class CompletionParams(baseUnit: BaseUnit,
                            propertyMappings: Seq[PropertyMapping],
                            position: Position,
                            prefix: String,
                            amfObject: AmfObject,
                            dialect: Dialect,
                            yPartBranch: YPartBranch,
                            fieldEntry: Option[FieldEntry]) {

  lazy val declarationProvider: DeclarationProvider =
    DeclarationProvider(baseUnit, Some(dialect))
}

object RequestToCompletionParams {
  implicit class RequestConverter(request: CompletionRequest) {
    def toParams(linePrefix: String): CompletionParams = {
      CompletionParams(request.baseUnit,
                       request.propertyMapping,
                       request.position,
                       linePrefix,
                       request.amfObject,
                       request.actualDialect,
                       request.yPartBranch,
                       request.fieldEntry)
    }
  }
}
