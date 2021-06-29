package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.ParameterModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect.DialectLocation
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{DialectNode, Oas30VariableObject}

object AsyncApiVariableObject extends DialectNode {

  override def name: String = "VariableObject"

  override def nodeTypeMapping: String = ParameterModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] =
    Oas30VariableObject.properties :+ PropertyMapping()
      .withId(DialectLocation + "#/declarations/VariableObject/examples")
      .withName("examples")
      .withNodePropertyMapping(DialectLocation + "#/declarations/VariableObject/examples")
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri())
}
