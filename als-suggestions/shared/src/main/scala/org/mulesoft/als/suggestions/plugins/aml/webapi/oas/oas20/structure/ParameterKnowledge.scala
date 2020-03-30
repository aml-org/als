package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure

import org.mulesoft.als.common.YPartBranch

trait ParameterKnowledge {
  def isInParameter(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKeyDescendantOf("parameters") || (yPartBranch.isJson && yPartBranch.isInArray && yPartBranch
      .parentEntryIs("parameters"))
}
