package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect

trait AMLHeaderObjectDialect extends AMLOasParamBaseObject {

  override val name: String            = "HeaderObject"
  override val nodeTypeMapping: String = "http://HeaderObject/#mapping"

}

object Oas20AMLHeaderObject extends AMLHeaderObjectDialect {
  override val properties: Seq[PropertyMapping] = paramProperties ++ Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/ParameterObject/type")
      .withName("type")
      .withMinCount(1)
      .withNodePropertyMapping(OasBaseDialect.DialectLocation + "#/declarations/ParameterObject/type")
      .withEnum(
        Seq(
          "string",
          "number",
          "integer",
          "boolean",
          "array",
          "file"
        ))
      .withLiteralRange(xsdString.iri())
  )
}

object Oas30AMLHeaderObject extends AMLOas30BaseParamProps with AMLHeaderObjectDialect {

  override val properties: Seq[PropertyMapping] = specialProps :+ description
}
