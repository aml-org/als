package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.ParameterModel
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
