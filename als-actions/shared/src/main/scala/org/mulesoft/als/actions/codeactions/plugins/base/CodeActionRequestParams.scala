package org.mulesoft.als.actions.codeactions.plugins.base

import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.feature.codeactions.CodeActionParams
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

case class CodeActionRequestParams(uri: String,
                                   range: PositionRange,
                                   bu: BaseUnit,
                                   telemetryProvider: TelemetryProvider,
                                   uuid: String)

object CodeActionParamsImpl {
  implicit class CodeActionParamsImpl(param: CodeActionParams) {
    def toRequestParams(bu: BaseUnit, telemetryProvider: TelemetryProvider, uuid: String): CodeActionRequestParams =
      CodeActionRequestParams(param.textDocument.uri, PositionRange(param.range), bu, telemetryProvider, uuid)
  }
}
