package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.AnnotationMappingModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString, xsdUri}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object AnnotationMappingObjectNode extends DialectNode {
  override def name: String = "AnnotationMappingObjectNode"

  override def nodeTypeMapping: String = AnnotationMappingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(location + s"#/declarations/$name/domain")
        .withNodePropertyMapping(AnnotationMappingModel.Domain.value.iri())
        .withName("domain")
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/propertyTerm")
        .withNodePropertyMapping(AnnotationMappingModel.NodePropertyMapping.value.iri())
        .withName("propertyTerm")
        .withMinCount(1)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/range")
        .withNodePropertyMapping(AnnotationMappingModel.NodePropertyMapping.value.iri())
        .withName("range")
        .withMinCount(1)
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
        .withId(location + s"#/declarations/$name/mandatory")
        .withNodePropertyMapping(AnnotationMappingModel.MinCount.value.iri())
        .withName("mandatory")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/pattern")
        .withNodePropertyMapping(AnnotationMappingModel.Pattern.value.iri())
        .withName("pattern")
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/minimum")
        .withNodePropertyMapping(AnnotationMappingModel.Minimum.value.iri())
        .withName("minimum")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/maximum")
        .withNodePropertyMapping(AnnotationMappingModel.Maximum.value.iri())
        .withName("maximum")
        .withLiteralRange(xsdInteger.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/unique")
        .withNodePropertyMapping(AnnotationMappingModel.Unique.value.iri())
        .withName("unique")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/allowMultiple")
        .withNodePropertyMapping(AnnotationMappingModel.AllowMultiple.value.iri())
        .withName("allowMultiple")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/sorted")
        .withNodePropertyMapping(AnnotationMappingModel.Sorted.value.iri())
        .withName("sorted")
        .withLiteralRange(xsdBoolean.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/enum")
        .withNodePropertyMapping(AnnotationMappingModel.Enum.value.iri())
        .withName("enum")
        .withAllowMultiple(true)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/typeDiscriminator")
        .withNodePropertyMapping(AnnotationMappingModel.TypeDiscriminator.value.iri())
        .withName("typeDiscriminator")
        .withAllowMultiple(true)
        .withLiteralRange(xsdString.iri()),
      PropertyMapping()
        .withId(location + s"#/declarations/$name/typeDiscriminatorName")
        .withNodePropertyMapping(AnnotationMappingModel.TypeDiscriminator.value.iri())
        .withName("typeDiscriminatorName")
        .withAllowMultiple(true)
        .withLiteralRange(xsdString.iri()),
      )
}
