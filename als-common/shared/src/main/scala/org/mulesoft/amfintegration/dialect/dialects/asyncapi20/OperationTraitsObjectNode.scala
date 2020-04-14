package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.dialects.oas.nodes.{AMLExternalDocumentationObject, AMLTagObject, DialectNode}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.metamodel.bindings.{OperationBindingModel, OperationBindingsModel}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.OperationBindingObjectNode

trait OperationAbstractObjectNode extends DialectNode{

  override def nodeTypeMapping: String = OperationModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Operation/operationId")
      .withName("operationId")
      .withNodePropertyMapping(OperationModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Operation/summary")
      .withName("summary")
      .withNodePropertyMapping(OperationModel.Summary.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Operation/description")
      .withName("description")
      .withNodePropertyMapping(OperationModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Operation/tags")
      .withName("tags")
      .withNodePropertyMapping(OperationModel.Tags.value.iri())
      .withObjectRange(Seq(AMLTagObject.id))
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(location + "#/declarations/Operation/externalDocs")
      .withName("externalDocs")
      .withNodePropertyMapping(OperationModel.Documentation.value.iri())
      .withObjectRange(Seq(AMLExternalDocumentationObject.id))
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(location + "#/declarations/Operation/bindings")
      .withName("bindings")
      .withNodePropertyMapping(OperationBindingsModel.`type`.head.iri())
      .withObjectRange(Seq(OperationBindingObjectNode.id)),
  )
}

object OperationTraitsObjectNode extends OperationAbstractObjectNode{
  override def name: String = "OperationTraitsObjectNode"

  override def isAbstract: Boolean = true
}