package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.domain.{AnnotationMapping, NodeMapping, UnionNodeMapping}
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.YPartBranch

package object metadialect {
  def isNodeMappable(x: AmfObject): Boolean =
    x.isInstanceOf[UnionNodeMapping] ||
      x.isInstanceOf[NodeMapping] ||
      x.isInstanceOf[AnnotationMapping]

  def isClassTermOrDomain(amfObject: AmfObject, yPartBranch: YPartBranch): Boolean = {
    isNodeMappable(amfObject) &&
    (yPartBranch.parentEntryIs("classTerm") ||
    yPartBranch.parentEntryIs("domain"))
  }
}
