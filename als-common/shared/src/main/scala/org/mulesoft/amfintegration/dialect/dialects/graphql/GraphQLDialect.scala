package org.mulesoft.amfintegration.dialect.dialects.graphql

import amf.aml.client.scala.model.domain.DocumentsModel
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object GraphQLDialect extends BaseDialect {
  private val _ = Raml10TypesDialect().id // hack for ExampleNode.id

  override def DialectLocation: String = "file://vocabularies/dialects/graphql.yaml"

  override protected val name: String    = "graphql"
  override protected val version: String = "1.0.0"

  override protected def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withId(DialectLocation + "#/documents")
//      .withKeyProperty(true)
      .withReferenceStyle(ReferenceStyles.JSONSCHEMA)

  override protected def encodes: DialectNode = TypeSchemaNode

  override val declares: Seq[DialectNode] = Nil

  override protected def declaredNodes: Map[String, DialectNode] = Map.empty
}
