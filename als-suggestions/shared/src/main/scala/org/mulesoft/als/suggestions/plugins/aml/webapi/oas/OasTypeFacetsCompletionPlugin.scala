package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfmanager.dialect.webapi.oas.{JsonSchemaForOasWrapper, Oas20DialectWrapper}

trait OasTypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {
  def jsonSchameObj: JsonSchemaForOasWrapper
  def dialect: Dialect

  override def stringShapeNode: NodeMapping = jsonSchameObj.StringSchemaObject

  override def numberShapeNode: NodeMapping = jsonSchameObj.NumberSchemaObject

  override def integerShapeNode: NodeMapping = jsonSchameObj.IntegerSchemaObject

  override def declarations: Seq[NodeMapping] = dialect.declares.collect({ case n: NodeMapping => n })

  override def propertyShapeNode: Option[NodeMapping] = None

  override def id: String = "OasTypeFacetsCompletionPlugin"
}
