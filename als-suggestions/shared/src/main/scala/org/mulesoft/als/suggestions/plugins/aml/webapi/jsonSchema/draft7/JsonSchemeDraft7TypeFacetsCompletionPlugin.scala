package org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema.draft7

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.scala.model.domain.Shape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.NumberShapeAsync2Node
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.base.StringShapeJsonSchemaNode
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect

object JsonSchemeDraft7TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {
  override def id: String = "Async20JsonSchemeDraft7TypeFacetsCompletionPlugin"

  val dialect: Dialect = JsonSchemaDraft7Dialect.dialect

  override def stringShapeNode: NodeMapping = new StringShapeJsonSchemaNode().Obj

  override def numberShapeNode: NodeMapping = NumberShapeAsync2Node.Obj

  override def integerShapeNode: NodeMapping = NumberShapeAsync2Node.Obj

  def propertyShapeNode: Option[NodeMapping] = None

  def declarations: Seq[NodeMapping] =
    dialect.declares.collect({ case n: NodeMapping => n })

  override protected def defaults(s: Shape): Seq[RawSuggestion] = Seq()
}
