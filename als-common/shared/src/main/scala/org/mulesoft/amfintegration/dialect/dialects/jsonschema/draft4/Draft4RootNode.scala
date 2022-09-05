package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base._

object Draft4RootNode extends Draft4RootNode

trait Draft4RootNode extends BaseJsonSchemaDocumentNode {

  override def properties: Seq[PropertyMapping] =
    super.properties ++
      BaseAnyShapeNode.anyShapeFacets(location) ++
      BaseArrayShapeNode.arrayShapeFacets(location) ++
      BaseNodeShapeNode.nodeShapeFacets(location) ++
      BaseNumberShapeNode.numberShapeFacets(location) ++
      BaseStringShapeNode.stringShapeFacets(location) :+
      identifierMapping(location)

  def identifierMapping(location: String): PropertyMapping =
    PropertyMapping()
      .withId(location + "#/declarations/NodeShapeNode/id")
      .withNodePropertyMapping(WebApiModel.Identifier.value.iri())
      .withName("id")
      .withLiteralRange(xsdString.iri())
}
