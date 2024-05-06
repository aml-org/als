package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.ServerModel
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApiServerObject
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.AMLTagObject

object AsyncApi26ServerObject extends AsyncApiServerObject {
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/ServerObject/tags")
      .withName("tags")
      .withAllowMultiple(true)
      .withNodePropertyMapping(ServerModel.Tags.value.iri())
      .withObjectRange(Seq(AMLTagObject.id))
  )
}
