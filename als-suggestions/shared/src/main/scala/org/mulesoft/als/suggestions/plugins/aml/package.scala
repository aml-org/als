package org.mulesoft.als.suggestions.plugins

import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

package object aml {

  implicit class PropertyMappingWrapper(p: PropertyMapping) {
    def toRaw(indentation: String, category: String): RawSuggestion = {
      if (p.objectRange().nonEmpty || p.allowMultiple().value())
        if (p.allowMultiple().value() && p.mapTermKeyProperty().option().isEmpty)
          RawSuggestion.keyOfArray(p.name().value(), indentation, category)
        else
          RawSuggestion(p.name().value(), indentation, isAKey = true, category = category)
      else
        RawSuggestion.forKey(p.name().value(), category = category)
    }
  }

  implicit class NodeMappingWrapper(nodeMapping: NodeMapping) {

    def propertiesRaw(indentation: String, category: Option[String] = None): Seq[RawSuggestion] =
      nodeMapping.propertiesMapping().map { p =>
        val c = category.getOrElse(
          CategoryRegistry(nodeMapping.meta.`type`.headOption.map(_.iri()).getOrElse(""), p.name().value()))
        p.toRaw(indentation, c)
      }
  }
}
