package org.mulesoft.als.common

import amf.core.model.domain.{AmfObject, AmfScalar, DomainElement, NamedDomainElement}
import amf.core.vocabulary.Namespace
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel

object ElementNameExtractor {

  implicit class ElementNameExtractor(element: AmfObject) {

    def elementIdentifier(): Option[String] =
      element.fields
        .getValueAsOption(DialectDomainElementModel.DeclarationName)
        .map(_.value)
        .orElse(
          element.fields
            .fields()
            .find(fe => fe.field.value.iri() == (Namespace.Core + "name").iri())
            .map(_.value.value))
        .collect({ case s: AmfScalar => s.value.toString })
        .orElse(
          element match {
            case n: NamedDomainElement => n.name.option()
            case _                     => None
          }
        )
  }

}
