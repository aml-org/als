package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.EndPointModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}

abstract class AMLPathItemObject extends DialectNode {

  override def name: String            = "PathItemObject"
  override def nodeTypeMapping: String = EndPointModel.`type`.head.iri()
  def operationObjID: String

  def versionProperties: Seq[PropertyMapping]

  override def properties: Seq[PropertyMapping] = versionProperties ++ Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/name")
      .withNodePropertyMapping(EndPointModel.Path.value.iri())
      .withObjectRange(Seq("null")),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/get")
      .withName("get")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/put")
      .withName("put")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/post")
      .withName("post")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/delete")
      .withName("delete")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/options")
      .withName("options")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/head")
      .withName("head")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/patch")
      .withName("patch")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID))
  )

}

object Oas20PathItemObject extends AMLPathItemObject {
  override def operationObjID: String = Oas20AMLOperationObject.id
  override def location: String       = OAS20Dialect.DialectLocation
  override def versionProperties: Seq[PropertyMapping] =
    Seq(
      PropertyMapping()
        .withId(DialectLocation + "#/declarations/PathItem/parameters")
        .withName("parameters")
        .withNodePropertyMapping(EndPointModel.Parameters.value.iri())
        .withAllowMultiple(true)
        .withObjectRange(
          Seq(
            Oas20ParamObject.id,
            Oas20BodyParameterObject.id
          )
        )
    )
}

object Oas30PathItemObject extends AMLPathItemObject {

  override def location: String       = OAS30Dialect.DialectLocation
  override def operationObjID: String = Oas30OperationObject.id
  override def versionProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/summary")
      .withName("summary")
      .withNodePropertyMapping(EndPointModel.Summary.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/description")
      .withName("description")
      .withNodePropertyMapping(EndPointModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/trace")
      .withName("trace")
      .withNodePropertyMapping(EndPointModel.Operations.value.iri())
      .withObjectRange(Seq(operationObjID)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/servers")
      .withName("servers")
      .withNodePropertyMapping(EndPointModel.Servers.value.iri())
      .withObjectRange(Seq(Oas30ServerObject.id))
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/PathItem/parameters")
      .withName("parameters")
      .withNodePropertyMapping(EndPointModel.Parameters.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(
        Seq(
          Oas30PathItemObject.id
        )
      )
  )
}
