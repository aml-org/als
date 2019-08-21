package org.mulesoft.als.suggestions.plugins

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

package object aml {
  implicit class NodeMappingWrapper(nodeMapping: NodeMapping) {
//    def propertiesNames: Seq[String] = nodeMapping.propertiesMapping().flatMap(_.name().option())

    def propertiesRaw(indentation: String, category: Option[String] = None): Seq[RawSuggestion] =
      nodeMapping.propertiesMapping().map { p =>
        if (p.objectRange().nonEmpty || p.allowMultiple().value())
          RawSuggestion(p.name().value(),
                        indentation,
                        isAKey = true,
                        category = CategoryRegistry(nodeMapping.meta.`type`.headOption.map(_.iri()).getOrElse(""),
                                                    p.name().value()))
        else
          RawSuggestion.forKey(
            p.name().value(),
            category =
              CategoryRegistry(nodeMapping.meta.`type`.headOption.map(_.iri()).getOrElse(""), p.name().value()))
      }
  }
}
