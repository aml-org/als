package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.CallbackModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation

object AMLCallbackObject extends DialectNode {

  override def name: String            = "CallbackObject"
  override def nodeTypeMapping: String = CallbackModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/CallbackObject/name")
      .withName("name")
      .withNodePropertyMapping(CallbackModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/CallbackObject/expression")
      .withName("expression")
      .withNodePropertyMapping(CallbackModel.Expression.value.iri())
      .withObjectRange(
        Seq(
          Oas30PathItemObject.id
        ))
  )
}
