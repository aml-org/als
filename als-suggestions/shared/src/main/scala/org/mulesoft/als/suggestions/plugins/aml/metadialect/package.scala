package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.domain.{AnnotationMapping, NodeMapping, PropertyLikeMapping, UnionNodeMapping}
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}

package object metadialect {
  def isNodeMappable(x: AmfObject): Boolean =
    x.isInstanceOf[UnionNodeMapping] ||
      x.isInstanceOf[NodeMapping] ||
      x.isInstanceOf[AnnotationMapping]

  def isClassTermOrDomain(amfObject: AmfObject, astPart: ASTPartBranch): Boolean =
    isNodeMappable(amfObject) &&
      (astPart.parentEntryIs("classTerm") ||
        astPart.parentEntryIs("domain"))

  def isPropTerm(amfObject: AmfObject, astPart: ASTPartBranch): Boolean =
    amfObject.isInstanceOf[PropertyLikeMapping[_]] &&
      (astPart.parentEntryIs("propertyTerm") ||
        astPart.parentEntryIs("mapTermKey") ||
        astPart.parentEntryIs("mapTermValue"))

  def isTerm(amfObject: AmfObject, astPart: ASTPartBranch): Boolean =
    isPropTerm(amfObject, astPart) || isClassTermOrDomain(amfObject, astPart)
}
