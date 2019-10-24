package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfmanager.dialect.webapi.oas.Oas20DialectWrapper

object OasTypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {
  override def stringShapeNode: NodeMapping = Oas20DialectWrapper.JsonSchemas.StringSchemaObject

  override def numberShapeNode: NodeMapping = Oas20DialectWrapper.JsonSchemas.NumberSchemaObject

  override def integerShapeNode: NodeMapping = Oas20DialectWrapper.JsonSchemas.IntegerSchemaObject

  override def declarations: Seq[NodeMapping] =
    Oas20DialectWrapper.dialect.declares.collect({ case n: NodeMapping => n })

  override def propertyShapeNode: Option[NodeMapping] = None

  override def id: String = "OasTypeFacetsCompletionPlugin"
}
