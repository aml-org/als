package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.domain.{NodeMapping, UnionNodeMapping}
import amf.core.client.scala.model.domain.AmfObject

package object metadialect {
  def isNodeMappable(x: AmfObject): Boolean =
    x.isInstanceOf[UnionNodeMapping] ||
      x.isInstanceOf[NodeMapping]
}
