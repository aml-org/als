package org.mulesoft.amfintegration.dialect.dialects.oas

import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.vocabulary.Namespace
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

import scala.language.implicitConversions

trait OasBaseDialect {

  implicit def toNode(obj: DialectNode): DomainElement = obj.Obj

  // Base location for all information in the OAS20 dialect
  def DialectLocation = "file://vocabularies/dialects/oas.yaml"

  // This will be used to mark collapsed nodes, like WebAPIObject and InfoObject merged into the WebAPI node in the model
  val OwlSameAs: String = (Namespace.Owl + "sameAs").iri()

  // Marking syntactic fields in the AST that are not directly mapped to properties in the mdoel
  val ImplicitField: String = (Namespace.Meta + "implicit").iri()

}

object OasBaseDialect extends OasBaseDialect
