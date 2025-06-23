package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon.{
  declaredElementNode,
  renderNode
}
import org.mulesoft.als.common.YPartASTWrapper.YNodeImplicits
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.amfconfiguration.{ALSConfigurationState, DocumentDefinition}
import org.yaml.model.YMapEntry
import org.yaml.render.{JsonRenderOptions, YamlRenderOptions}

trait DeclarationWrapper {

  val declarationKey: String

  def wrapDeclaration(
                       amfObject: Option[AmfObject],
                       newName: String,
                       bu: BaseUnit,
                       uri: String,
                       documentDefinition: DocumentDefinition,
                       configurationReader: AlsConfigurationReader,
                       jsonOptions: JsonRenderOptions,
                       yamlOptions: YamlRenderOptions,
                       alsConfigurationState: ALSConfigurationState
  ): Option[(String, Option[YMapEntry])] = {
    val existingDeclaration = ExtractorCommon.findExistingKeyPart(bu, uri, Seq(declarationKey))
    declaredElementNode(amfObject, documentDefinition, alsConfigurationState)
      .map(node => {
        val r = node.withKey(newName)
        if (existingDeclaration.isEmpty) r.withKey(declarationKey) else r
      })
      .map { node =>
        renderNode(node, existingDeclaration.headOption, bu, configurationReader, jsonOptions, yamlOptions)
      }
  }

}
