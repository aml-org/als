package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect

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
