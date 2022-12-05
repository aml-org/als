package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure

import amf.apicontract.internal.annotations.FormBodyParameter
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.ASTPartBranch

trait ParameterKnowledge {
  def isInParameter(astPartBranch: ASTPartBranch): Boolean =
    // todo: check if the alternative "if strict" is still necessary or just a leftover from json patcher
    astPartBranch.isKeyDescendantOf("parameters") || (astPartBranch.strict && astPartBranch.isInArray && astPartBranch
      .parentEntryIs("parameters"))

  def isInParameter(amfObject: AmfObject): Boolean =
    amfObject.annotations.contains(classOf[FormBodyParameter])
}
