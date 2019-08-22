package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin

object RamlTypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {
  override def id: String = "RamlTypeFacetsCompletionPlugin"

  override def stringShapeNode: NodeMapping = Raml10TypesDialect.StringShapeNode

  override def numberShapeNode: NodeMapping = Raml10TypesDialect.NumberShapeNode

  override def integerShapeNode: NodeMapping = Raml10TypesDialect.NumberShapeNode

  override def declarations: Seq[NodeMapping] =
    Raml10TypesDialect.dialect.declares.collect({ case n: NodeMapping => n })

  override def propertyShapeNode: Option[NodeMapping] = Some(Raml10TypesDialect.PropertyShapeNode)
}
