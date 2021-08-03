package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect
import amf.plugins.document.vocabularies.model.domain.{NodeMappable, NodeMapping, PropertyMapping}

trait DialectNode {

  def location: String = OasBaseDialect.DialectLocation

  def name: String
  def id: String = location + "/#declarations/" + name
  def nodeTypeMapping: String
  def isAbstract = false

  def properties: Seq[PropertyMapping]

  private def getTypeMappingUri: String = if (isAbstract) nodeTypeMapping + "Abstract" else nodeTypeMapping

  lazy val Obj: NodeMapping = NodeMapping()
    .withId(id)
    .withName(name)
    .withNodeTypeMapping(getTypeMappingUri)
    .withPropertiesMapping(properties)

}
