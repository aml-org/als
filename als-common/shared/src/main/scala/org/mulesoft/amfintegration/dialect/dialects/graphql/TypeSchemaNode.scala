package org.mulesoft.amfintegration.dialect.dialects.graphql

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object TypeSchemaNode extends DialectNode {
  override def name: String = "TypeSchemaNode"

  override def nodeTypeMapping: String = WebApiModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/GraphQLObject/schema")
      .withName("schema")
      .withMinCount(1)
      .withNodePropertyMapping(SchemaNode.id)
      .withObjectRange(
        Seq(
          SchemaNode.id
        )
      ),
    PropertyMapping()
      .withId(location + "#/declarations/GraphQLObject/type")
      .withName("type")
      .withMinCount(1)
      .withNodePropertyMapping(TypeNode.id)
      .withObjectRange(
        Seq(
          TypeNode.id
        )
      ),
    PropertyMapping()
      .withId(location + "#/declarations/GraphQLObject/input")
      .withName("input")
      .withMinCount(1)
      .withNodePropertyMapping(TypeNode.id)
      .withObjectRange(
        Seq(
          TypeNode.id
        )
      )
  )
}

object SchemaNode extends DialectNode {
  override def name: String = "SchemaNode"

  override def nodeTypeMapping: String = "schemaNode/id"

  override def properties: Seq[PropertyMapping] = Nil
}

object TypeNode extends DialectNode {
  override def name: String = "TypeNode"

  override def nodeTypeMapping: String = "typeNode/id"

  override def properties: Seq[PropertyMapping] = Nil
}
