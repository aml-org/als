package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure

import amf.apicontract.internal.annotations.FormBodyParameter
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.YPartBranch

trait ParameterKnowledge {
  def isInParameter(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKeyDescendantOf("parameters") || (yPartBranch.isJson && yPartBranch.isInArray && yPartBranch
      .parentEntryIs("parameters"))

  def isInParameter(amfObject: AmfObject): Boolean =
    amfObject.annotations.contains(classOf[FormBodyParameter])
}
