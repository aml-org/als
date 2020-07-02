package org.mulesoft.als.suggestions.plugins.aml.webapi

import org.mulesoft.als.common.YPartBranch

trait IsInsideRequired {
  def isInsideRequired(yPartBranch: YPartBranch): Boolean =
    (yPartBranch.isKeyDescendantOf("required") || ((yPartBranch.isValue || yPartBranch.isArray || (yPartBranch.stringValue == "x" && yPartBranch.isInArray)) && yPartBranch
      .parentEntryIs("required"))) || yPartBranch.parentEntryIs("properties")
}
