package org.mulesoft.amfintegration.dialect.dialects.raml.raml08

import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain._
import org.mulesoft.amfintegration.dialect.dialects.raml.RamlDialect

object Raml08Dialect extends RamlDialect {

  override val dialectLocation = "file://vocabularies/dialects/raml08.yaml"

  // Marking syntactic fields in the AST that are not directly mapped to properties in the model

  override protected lazy val rootId: String = Raml08DialectNodes.RootNode.id
  override protected lazy val dialectDeclares: Seq[NodeMapping] = Seq(
    Raml08DialectNodes.ExampleNode,
    Raml08DialectNodes.DataTypeNode,
    Raml08DialectNodes.DocumentationNode,
    Raml08DialectNodes.PayloadNode,
    Raml08DialectNodes.ResourceTypeNode,
    Raml08DialectNodes.TraitNode,
    Raml08DialectNodes.ResponseNode,
    Raml08DialectNodes.MethodNode,
    Raml08DialectNodes.ResourceNode,
    Raml08DialectNodes.RootNode
  )

  override protected val version: String = "0.8"
}