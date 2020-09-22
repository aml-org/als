package org.mulesoft.als.actions.codeactions.plugins.base

import amf.core.model.document.BaseUnit
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.als.common.cache.{ObjectInTreeCached, YPartBranchCached}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.lsp.feature.codeactions.CodeActionParams
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.Future

case class CodeActionRequestParams(uri: String,
                                   range: PositionRange,
                                   bu: BaseUnit,
                                   tree: ObjectInTreeCached,
                                   yPartBranch: YPartBranchCached,
                                   dialect: Dialect,
                                   configuration: AlsConfigurationReader,
                                   allRelationships: Seq[RelationshipLink],
                                   telemetryProvider: TelemetryProvider,
                                   uuid: String)

object CodeActionParamsImpl {
  implicit class CodeActionParamsImpl(param: CodeActionParams) {
    def toRequestParams(bu: BaseUnit,
                        tree: ObjectInTreeCached,
                        yPartBranch: YPartBranchCached,
                        dialect: Dialect,
                        configuration: AlsConfigurationReader,
                        allRelationships: Seq[RelationshipLink],
                        telemetryProvider: TelemetryProvider,
                        uuid: String): CodeActionRequestParams =
      CodeActionRequestParams(param.textDocument.uri,
                              PositionRange(param.range),
                              bu,
                              tree,
                              yPartBranch,
                              dialect,
                              configuration,
                              allRelationships,
                              telemetryProvider,
                              uuid)
  }
}
