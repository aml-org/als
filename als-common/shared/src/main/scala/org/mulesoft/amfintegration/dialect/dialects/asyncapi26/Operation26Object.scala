package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.OperationModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.OperationObject

object Operation26Object extends OperationObject {
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/OperationObject/security")
      .withName("security")
      .withNodePropertyMapping(OperationModel.Security.value.iri())
      .withLiteralRange(xsdString.iri())
      .withAllowMultiple(true)
  )
}
