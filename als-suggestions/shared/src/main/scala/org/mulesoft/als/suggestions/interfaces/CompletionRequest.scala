package org.mulesoft.als.suggestions.interfaces

import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.dtoTypes.Position

trait CompletionRequest {

  val selectedNode: AmfObject

  val baseUnit: BaseUnit

  val position: Position

}
