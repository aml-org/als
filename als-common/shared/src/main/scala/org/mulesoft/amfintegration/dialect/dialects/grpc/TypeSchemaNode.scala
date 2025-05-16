package org.mulesoft.amfintegration.dialect.dialects.grpc

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object TypeSchemaNode extends DialectNode {
  override def name: String = "TypeSchemaNode"

  override def nodeTypeMapping: String = WebApiModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
  )
}