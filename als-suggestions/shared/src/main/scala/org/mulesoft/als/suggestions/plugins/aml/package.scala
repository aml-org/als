package org.mulesoft.als.suggestions.plugins

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

package object aml {

  implicit class PropertyMappingWrapper(p: PropertyMapping) {
    def toRaw(category: String): RawSuggestion = {
      if (p.objectRange().nonEmpty || p.allowMultiple().value())
        if (p.allowMultiple().value() && p.mapTermKeyProperty().option().isEmpty)
          RawSuggestion.keyOfArray(p.name().value(), category)
        else
          RawSuggestion.forObject(p.name().value(), category, p.minCount().value() > 0)
      else
        RawSuggestion.forKey(p.name().value(), category = category, p.minCount().value() > 0)
    }
  }

  implicit class NodeMappingWrapper(nodeMapping: NodeMapping) {

    def propertiesRaw(category: Option[String] = None, fromDialect: Dialect): Seq[RawSuggestion] =
      nodeMapping.propertiesMapping().map { p =>
        val c =
          category.getOrElse(CategoryRegistry(nodeMapping.nodetypeMapping.value(), p.name().value(), fromDialect.id))
        p.toRaw(c)
      }

  }
}
