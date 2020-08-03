package org.mulesoft.als.actions.codeactions.plugins.base

import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.PositionRange

case class CodeActionRequestParams(uri: String, range: PositionRange, bu: BaseUnit) {}
