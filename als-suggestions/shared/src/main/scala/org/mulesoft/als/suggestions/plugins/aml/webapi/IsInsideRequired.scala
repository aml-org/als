package org.mulesoft.als.suggestions.plugins.aml.webapi

import org.mulesoft.als.common.YPartBranch

trait IsInsideRequired {
  def isInsideRequired(yPartBranch: YPartBranch): Boolean =
    (yPartBranch.isDescendanceOf("required") || ((yPartBranch.isValue || yPartBranch.isArray || jsonPatchHack(
      yPartBranch)) && yPartBranch
      .parentEntryIs("required"))) || yPartBranch.parentEntryIs("properties")

  private def jsonPatchHack(yPartBranch: YPartBranch) =
    yPartBranch.stringValue == "x" && yPartBranch.isInArray
}
