package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.CallbackModel

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
