package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.document.vocabularies.model.domain.DocumentsModel
import org.mulesoft.amfintegration.dialect.BaseDialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.BaseShapeAsync2Node

object JsonSchemaDraft7Dialect extends BaseDialect {

  override val DialectLocation: String = dialectLocation

  override protected val name: String    = "Json Schema" // no this way right?
  override protected val version: String = "Draft 7"

  override protected def emptyDocument: DocumentsModel = DocumentsModel()

  override protected def encodes: DialectNode = BaseShapeAsync2Node

  override val declares: Seq[DialectNode] = Seq(
    NilShapeDraft7Node,
    ShapeDraft7Node,
    AnyShapeDraft7Node,
    ArrayShapeDraft7Node,
    NodeShapeDraft7Node,
    NumberShapeDraft7Node,
    StringShapeDraft7Node
  )

  override protected def declaredNodes: Map[String, DialectNode] = Map(
    "definitions" -> BaseShapeAsync2Node
  )
}
