package org.mulesoft.als.suggestions.plugins

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdDouble, xsdFloat, xsdInteger}
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry
import org.mulesoft.als.suggestions._

package object aml {

  implicit class PropertyMappingWrapper(p: PropertyMapping) {
    def toRaw(category: String): RawSuggestion = {
      if (p.objectRange().nonEmpty || p.allowMultiple().value())
        if (p.allowMultiple().value() && p.mapTermKeyProperty().option().isEmpty)
          RawSuggestion.keyOfArray(p.name().value(), category)
        else
          RawSuggestion.forObject(p.name().value(), category, p.minCount().value() > 0)
      else {
        val range: RangeKind = p.literalRange().option() match {
          case Some(value) if value == xsdBoolean.iri() => BoolScalarRange
          case Some(value) if (value == xsdDouble.iri() || value == xsdFloat.iri() || value == xsdInteger.iri()) => NumberScalarRange
          case _ => StringScalarRange
        }
        RawSuggestion.forKey(p.name().value(), category = category, p.minCount().value() > 0, rangeKind = range)
      }
    }
  }

  implicit class NodeMappingWrapper(nodeMapping: NodeMapping) {

    def propertiesRaw(category: Option[String] = None, fromDialect: Dialect): Seq[RawSuggestion] =
      nodeMapping
        .propertiesMapping()
        .filterNot(_.name().isNullOrEmpty) // todo: should centralize PropertyMappingFilter logic
        .map { p =>
          val c =
            category.getOrElse(CategoryRegistry(nodeMapping.nodetypeMapping.value(), p.name().value(), fromDialect.id))
          p.toRaw(c)
        }

  }
}
