package org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema.draft7

import amf.core.model.domain.Shape
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect

object JsonSchemeDraft7TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {
  override def id: String = "Async20JsonSchemeDraft7TypeFacetsCompletionPlugin"

  val dialect: Dialect = JsonSchemaDraft7Dialect.dialect

  override def stringShapeNode: NodeMapping = JsonSchemaDraft7Dialect.StringShapeNode

  override def numberShapeNode: NodeMapping = JsonSchemaDraft7Dialect.NumberShapeNode

  override def integerShapeNode: NodeMapping = JsonSchemaDraft7Dialect.NumberShapeNode

  def propertyShapeNode: Option[NodeMapping] = None

  def declarations: Seq[NodeMapping] =
    dialect.declares.collect({ case n: NodeMapping => n })

  override protected def defaults(s: Shape): Seq[RawSuggestion] = Seq()
}
