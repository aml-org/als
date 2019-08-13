package org.mulesoft.als.suggestions.plugins

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.RawSuggestion

package object aml {
  implicit class NodeMappingWrapper(nodeMapping: NodeMapping) {
//    def propertiesNames: Seq[String] = nodeMapping.propertiesMapping().flatMap(_.name().option())

    def propertiesRaw(identation: String): Seq[RawSuggestion] = {
      nodeMapping.propertiesMapping().map { p =>
        if (p.objectRange().nonEmpty || p.enum().nonEmpty)
          RawSuggestion(p.name().value(), identation, isAKey = true)
        else RawSuggestion.forKey(p.name().value())
      }
    }
  }
}
