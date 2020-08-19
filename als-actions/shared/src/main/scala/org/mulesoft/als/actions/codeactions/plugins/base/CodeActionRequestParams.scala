package org.mulesoft.als.actions.codeactions.plugins.base

import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.cache.{ObjectInTreeCached, YPartBranchCached}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.lsp.feature.codeactions.CodeActionParams
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

case class CodeActionRequestParams(uri: String,
                                   range: PositionRange,
                                   bu: BaseUnit,
                                   tree: ObjectInTreeCached,
                                   yPartBranch: YPartBranchCached,
                                   dialect: Option[Dialect],
                                   configuration: AlsConfigurationReader,
                                   telemetryProvider: TelemetryProvider,
                                   uuid: String)

object CodeActionParamsImpl {
  implicit class CodeActionParamsImpl(param: CodeActionParams) {
    def toRequestParams(bu: BaseUnit,
                        tree: ObjectInTreeCached,
                        yPartBranch: YPartBranchCached,
                        dialect: Option[Dialect],
                        configuration: AlsConfigurationReader,
                        telemetryProvider: TelemetryProvider,
                        uuid: String): CodeActionRequestParams =
      CodeActionRequestParams(param.textDocument.uri,
                              PositionRange(param.range),
                              bu,
                              tree,
                              yPartBranch,
                              dialect,
                              configuration,
                              telemetryProvider,
                              uuid)
  }
}
