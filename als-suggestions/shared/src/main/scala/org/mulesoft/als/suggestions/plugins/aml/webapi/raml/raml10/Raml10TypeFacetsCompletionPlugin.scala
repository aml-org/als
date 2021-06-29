package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10

import amf.aml.client.scala.model.domain.NodeMapping
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object Raml10TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin {
  override def id: String = "RamlTypeFacetsCompletionPlugin"

  override def stringShapeNode: NodeMapping = Raml10TypesDialect.StringShapeNode

  override def numberShapeNode: NodeMapping = Raml10TypesDialect.NumberShapeNode

  override def integerShapeNode: NodeMapping =
    Raml10TypesDialect.NumberShapeNode

  override def declarations: Seq[NodeMapping] =
    Raml10TypesDialect.dialect.declares.collect({ case n: NodeMapping => n })

  override def propertyShapeNode: Option[NodeMapping] =
    Some(Raml10TypesDialect.PropertyShapeNode)
}
