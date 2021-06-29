package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.PropertyMappingModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString, xsdUri}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object PropertyMappingObjectNode extends DialectNode{
  override def name: String = "PropertyMappingObjectNode"

  override def nodeTypeMapping: String = PropertyMappingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/name")
      .withNodePropertyMapping(PropertyMappingModel.Name.value.iri())
      .withName("name")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/propertyTerm")
      .withNodePropertyMapping(PropertyMappingModel.NodePropertyMapping.value.iri())
      .withName("propertyTerm")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/range")
      .withNodePropertyMapping(PropertyMappingModel.NodePropertyMapping.value.iri())
      .withName("range")
      .withLiteralRange(xsdUri.iri())
      .withEnum(Seq("string" ,
  "integer" ,
  "boolean" ,
  "float" ,
  "decimal" ,
  "double" ,
  "duration" ,
  "dateTime" ,

        "time" ,
  "date" ,
  "anyType",
        "anyUri",
        "link",
        "number",
        "uri",
        "any",
        "anyNode")),
    PropertyMapping()
    .withId(location + s"#/declarations/$name/mapKey")
      .withNodePropertyMapping(PropertyMappingModel.MapKeyProperty.value.iri())
      .withName("mapKey")
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mapTermKey")
      .withNodePropertyMapping(PropertyMappingModel.MapTermKeyProperty.value.iri())
      .withName("mapTermKey")
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mapValue")
      .withNodePropertyMapping(PropertyMappingModel.MapValueProperty.value.iri())
      .withName("mapValue")
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mapTermValue")
      .withNodePropertyMapping(PropertyMappingModel.MapTermValueProperty.value.iri())
      .withName("mapTermValue")
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/mandatory")
      .withNodePropertyMapping(PropertyMappingModel.MinCount.value.iri())
      .withName("mandatory")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/pattern")
      .withNodePropertyMapping(PropertyMappingModel.Pattern.value.iri())
      .withName("pattern")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/minimum")
      .withNodePropertyMapping(PropertyMappingModel.Minimum.value.iri())
      .withName("minimum")
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/maximum")
      .withNodePropertyMapping(PropertyMappingModel.Maximum.value.iri())
      .withName("maximum")
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/unique")
      .withNodePropertyMapping(PropertyMappingModel.Unique.value.iri())
      .withName("unique")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/allowMultiple")
      .withNodePropertyMapping(PropertyMappingModel.AllowMultiple.value.iri())
      .withName("allowMultiple")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/sorted")
      .withNodePropertyMapping(PropertyMappingModel.Sorted.value.iri())
      .withName("sorted")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/enum")
      .withNodePropertyMapping(PropertyMappingModel.Enum.value.iri())
      .withName("enum")
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/typeDiscriminator")
      .withNodePropertyMapping(PropertyMappingModel.TypeDiscriminator.value.iri())
      .withName("typeDiscriminator")
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/typeDiscriminatorName")
      .withNodePropertyMapping(PropertyMappingModel.TypeDiscriminator.value.iri())
      .withName("typeDiscriminatorName")
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
  )

}
