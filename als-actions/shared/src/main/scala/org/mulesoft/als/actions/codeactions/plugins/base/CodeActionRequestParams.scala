package org.mulesoft.als.actions.codeactions.plugins.base

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.cache.{ASTPartBranchCached, ObjectInTreeCached}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, DocumentDefinition}
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.lsp.feature.codeactions.CodeActionParams

case class CodeActionRequestParams(
                                    uri: String,
                                    range: PositionRange,
                                    bu: BaseUnit,
                                    tree: ObjectInTreeCached,
                                    astPartBranchCached: ASTPartBranchCached,
                                    documentDefinition: DocumentDefinition,
                                    configuration: AlsConfigurationReader,
                                    allRelationships: Seq[RelationshipLink],
                                    alsConfigurationState: ALSConfigurationState,
                                    uuid: String,
                                    directoryResolver: DirectoryResolver
) {

  val findDialectForSemantic: String => Option[(SemanticExtension, Dialect)] =
    alsConfigurationState.findSemanticByName
}

object CodeActionParamsImpl {
  implicit class CodeActionParamsImpl(param: CodeActionParams) {
    def toRequestParams(
                         bu: BaseUnit,
                         tree: ObjectInTreeCached,
                         astPartBranch: ASTPartBranchCached,
                         documentDefinition: DocumentDefinition,
                         configuration: AlsConfigurationReader,
                         allRelationships: Seq[RelationshipLink],
                         alsConfigurationState: ALSConfigurationState,
                         uuid: String,
                         directoryResolver: DirectoryResolver
    ): CodeActionRequestParams =
      CodeActionRequestParams(
        param.textDocument.uri,
        PositionRange(param.range),
        bu,
        tree,
        astPartBranch,
        documentDefinition,
        configuration,
        allRelationships,
        alsConfigurationState,
        uuid,
        directoryResolver
      )
  }
}
