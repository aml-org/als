package org.mulesoft.als.suggestions.plugins

import amf.plugins.document.vocabularies.model.domain.NodeMapping

package object aml {
  implicit class NodeMappingWrapper(nodeMapping: NodeMapping) {
    def propertiesNames: Seq[String] = nodeMapping.propertiesMapping().flatMap(_.name().option())
  }
}
