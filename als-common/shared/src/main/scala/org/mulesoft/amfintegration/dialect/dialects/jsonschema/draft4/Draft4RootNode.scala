package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base._

object Draft4RootNode extends Draft4RootNode

trait Draft4RootNode extends BaseJsonSchemaDocumentNode {

  override def properties: Seq[PropertyMapping] =
    super.properties ++
      BaseAnyShapeNode.anyShapeFacets ++
      BaseArrayShapeNode.arrayShapeFacets ++
      BaseNodeShapeNode.nodeShapeFacets ++
      BaseNumberShapeNode.numberShapeFacets ++
      BaseStringShapeNode.stringShapeFacets :+
      identifierMapping(location)

  def identifierMapping(location: String): PropertyMapping =
    PropertyMapping()
      .withId(location + "#/declarations/NodeShapeNode/id")
      .withNodePropertyMapping(WebApiModel.Identifier.value.iri())
      .withName("id")
      .withLiteralRange(xsdString.iri())
}
