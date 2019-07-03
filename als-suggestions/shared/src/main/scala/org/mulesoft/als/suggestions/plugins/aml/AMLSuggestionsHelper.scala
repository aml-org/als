package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.{AmfObject, DomainElement}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{DialectDomainElement, NodeMapping}

trait AMLSuggestionsHelper {
  def getDialectNode(dialect: Dialect, node: AmfObject): Option[DomainElement] = dialect.declares.find {
    case s: NodeMapping => s.nodetypeMapping.value() == node.meta.`type`.head.iri()
    case _              => false
  }

  def getDialectNode(dialect: Dialect, node: FieldEntry): Option[DomainElement] = dialect.declares.find {
    case s: NodeMapping =>
      s.nodetypeMapping.value() == (node.element match {
        case dde: DialectDomainElement => dde.meta.`type`.head.iri()
        case _                         => node.field.`type`.`type`.head.iri()
      })
    case _ => false
  }
}
