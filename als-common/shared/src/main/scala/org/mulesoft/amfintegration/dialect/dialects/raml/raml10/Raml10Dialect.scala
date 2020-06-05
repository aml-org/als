package org.mulesoft.amfintegration.dialect.dialects.raml.raml10

import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain._
import org.mulesoft.amfintegration.dialect.dialects.raml.RamlDialect

object Raml10Dialect extends RamlDialect {

  override val dialectLocation = "file://vocabularies/dialects/raml10.yaml"

  // Dialect
  override protected val version: String     = "1.0"
  override protected lazy val rootId: String = Raml10DialectNodes.RootNode.id
  override protected lazy val dialectDeclares: Seq[NodeMapping] = Seq(
    Raml10DialectNodes.ExampleNode,
    Raml10DialectNodes.DataTypeNode,
    Raml10DialectNodes.DocumentationNode,
    Raml10DialectNodes.PayloadNode,
    Raml10DialectNodes.ResourceTypeNode,
    Raml10DialectNodes.TraitNode,
    Raml10DialectNodes.ResponseNode,
    Raml10DialectNodes.MethodNode,
    Raml10DialectNodes.ResourceNode,
    Raml10DialectNodes.RootNode,
    Raml10DialectNodes.XmlNode
  )
}
