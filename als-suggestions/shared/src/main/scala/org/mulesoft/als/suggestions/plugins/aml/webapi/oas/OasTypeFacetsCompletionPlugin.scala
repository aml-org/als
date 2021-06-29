package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.JsonSchemaForOasWrapper

trait OasTypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {
  def jsonSchemaObj: JsonSchemaForOasWrapper
  def dialect: Dialect

  override def stringShapeNode: NodeMapping = jsonSchemaObj.StringSchemaObject

  override def numberShapeNode: NodeMapping = jsonSchemaObj.NumberSchemaObject

  override def integerShapeNode: NodeMapping = jsonSchemaObj.IntegerSchemaObject

  override def declarations: Seq[NodeMapping] = dialect.declares.collect({ case n: NodeMapping => n })

  override def propertyShapeNode: Option[NodeMapping] = None

  override def id: String = "OasTypeFacetsCompletionPlugin"
}
