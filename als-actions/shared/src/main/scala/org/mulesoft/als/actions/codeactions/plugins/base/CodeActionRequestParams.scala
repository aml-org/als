package org.mulesoft.als.actions.codeactions.plugins.base

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.cache.{ObjectInTreeCached, YPartBranchCached}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.lsp.feature.codeactions.CodeActionParams
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

case class CodeActionRequestParams(uri: String,
                                   range: PositionRange,
                                   bu: BaseUnit,
                                   tree: ObjectInTreeCached,
                                   yPartBranch: YPartBranchCached,
                                   dialect: Dialect,
                                   configuration: AlsConfigurationReader,
                                   allRelationships: Seq[RelationshipLink],
                                   telemetryProvider: TelemetryProvider,
                                   uuid: String,
                                   amfConfiguration: AmfConfigurationWrapper,
                                   directoryResolver: DirectoryResolver) {
  val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    amfConfiguration.findSemanticByName
}

object CodeActionParamsImpl {
  implicit class CodeActionParamsImpl(param: CodeActionParams) {
    def toRequestParams(bu: BaseUnit,
                        tree: ObjectInTreeCached,
                        yPartBranch: YPartBranchCached,
                        dialect: Dialect,
                        configuration: AlsConfigurationReader,
                        allRelationships: Seq[RelationshipLink],
                        telemetryProvider: TelemetryProvider,
                        uuid: String,
                        amfConfiguration: AmfConfigurationWrapper,
                        directoryResolver: DirectoryResolver): CodeActionRequestParams =
      CodeActionRequestParams(
        param.textDocument.uri,
        PositionRange(param.range),
        bu,
        tree,
        yPartBranch,
        dialect,
        configuration,
        allRelationships,
        telemetryProvider,
        uuid,
        amfConfiguration,
        directoryResolver
      )
  }
}
