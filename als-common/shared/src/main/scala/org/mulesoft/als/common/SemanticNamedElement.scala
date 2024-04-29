package org.mulesoft.als.common

import amf.aml.internal.metamodel.domain.DialectDomainElementModel
import amf.core.client.scala.model.domain.{AmfObject, AmfScalar, NamedDomainElement}
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.parser.domain.Value

object SemanticNamedElement {

  implicit class ElementNameExtractor(element: AmfObject) {

    def elementIdentifier(): Option[String] =
      namedField()
        .collect({ case Value(s: AmfScalar, _) => s.value.toString })
        .orElse(
          element match {
            case n: NamedDomainElement => n.name.option()
            case _                     => None
          }
        )

    def namedField(): Option[Value] = {
      element.fields
        .getValueAsOption(DialectDomainElementModel.DeclarationName)
        .orElse(
          element.fields
            .fields()
            .find(fe => nameIris.contains(fe.field.value.iri()))
            .map(_.value)
        )
    }
  }
  private val nameIris = Seq(
    (Namespace.Core + "name").iri(),
    (Namespace.Shapes + "name").iri(),
    (Namespace.Shacl + "name").iri(),
    "https://schema.org#name"
  )
}
