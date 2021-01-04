package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.remote.Vendor
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.ExtractorCommon.{
  declaredElementNode,
  emitElement,
  renderNode
}
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.lsp.feature.codeactions.CodeActionParams
import org.yaml.model.{YMapEntry, YNode}
import org.mulesoft.als.common.YamlWrapper.YNodeImplicits
import org.yaml.render.{JsonRenderOptions, YamlRenderOptions}

trait DeclarationWrapper {

  val declarationKey: String

  def wrapDeclaration(amfObject: Option[AmfObject],
                      newName: String,
                      bu: BaseUnit,
                      uri: String,
                      vendor: Vendor,
                      dialect: Dialect,
                      configurationReader: AlsConfigurationReader,
                      jsonOptions: JsonRenderOptions,
                      yamlOptions: YamlRenderOptions): Option[(String, Option[YMapEntry])] = {
    val existingDeclaration = ExtractorCommon.findExistingKeyPart(bu, uri, Seq(declarationKey))
    declaredElementNode(amfObject, vendor, dialect)
      .map(node => {
        val r = node.withKey(newName)
        if (existingDeclaration.isEmpty) r.withKey(declarationKey) else r
      })
      .map { node =>
        renderNode(node, existingDeclaration.headOption, bu, configurationReader, jsonOptions, yamlOptions)
      }
  }

}
