package org.mulesoft.amfintegration.dialect.dialects.jsonschema

import amf.aml.client.scala.model.domain.{DocumentsModel, PropertyMapping}
import amf.core.internal.remote.Spec
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base._
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

abstract class JsonSchemaBaseDialect extends BaseDialect {

  override def DialectLocation: String

  override protected val name: String = Spec.JSONSCHEMA.toString
  override protected val version: String

  override protected def emptyDocument: DocumentsModel =
    DocumentsModel()
      .withReferenceStyle(ReferenceStyles.JSONSCHEMA)

  protected def baseProps(location: String): Seq[PropertyMapping] = Nil

  protected val nilShape: DialectNode   = new NilShapeJsonSchemaNode(DialectLocation, baseProps)
  protected val shapeNode: DialectNode  = new ShapeJsonSchemaNode(DialectLocation, baseProps)
  protected val anyShape: DialectNode   = new AnyShapeJsonSchemaNode(DialectLocation, baseProps)
  protected val arrayShape: DialectNode = new ArrayShapeJsonSchemaNode(DialectLocation, baseProps)
  protected val nodeShape: DialectNode  = new NodeShapeJsonSchemaNode(DialectLocation, baseProps)
  protected val numberNode: DialectNode = new NumberShapeJsonSchemaNode(DialectLocation, baseProps)
  protected val stringNode: DialectNode = new StringShapeJsonSchemaNode(DialectLocation, baseProps)

  override val declares: Seq[DialectNode] = Seq(
    nilShape,
    shapeNode,
    anyShape,
    arrayShape,
    nodeShape,
    numberNode,
    stringNode
  )

  override protected def declaredNodes: Map[String, DialectNode] = Map(
    "definitions" -> shapeNode
  )
}
