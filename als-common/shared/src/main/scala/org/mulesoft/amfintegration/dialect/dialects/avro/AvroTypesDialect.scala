package org.mulesoft.amfintegration.dialect.dialects.avro

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.vocabulary.Namespace
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdInteger, xsdString}
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect.DialectLocation

//REALLY NOT SURE ABOUT THIS CODE
object AvroTypesDialect {

  val StringShapeNode: NodeMapping = NodeMapping()
    .withId(DialectLocation + "#/declarations/StringShapeNode")
    .withName("StringShapeNode")
    .withNodeTypeMapping((Namespace.Shapes + "StringShape").iri())
    .withPropertiesMapping(
      Seq(
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ScalarShapeNode/pattern")
          .withNodePropertyMapping(ScalarShapeModel.Pattern.value.iri())
          .withName("pattern")
          .withLiteralRange(xsdString.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ScalarShapeNode/minLength")
          .withNodePropertyMapping(ScalarShapeModel.MinLength.value.iri())
          .withName("minLength")
          .withLiteralRange(xsdInteger.iri()),
        PropertyMapping()
          .withId(DialectLocation + "#/declarations/ScalarShapeNode/maxLength")
          .withNodePropertyMapping(ScalarShapeModel.MaxLength.value.iri())
          .withName("maxLength")
          .withLiteralRange(xsdInteger.iri())
      )
    )

//  val dialect: Dialect = {
//    val dialect = AvroDialect()
//    dialect.withDeclares(
//      dialect.declares ++
//        StringShapeNode
//    )
//  }
//
//  def apply(): Dialect = dialect
}
