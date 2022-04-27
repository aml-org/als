package org.mulesoft.als.suggestions.plugins.aml.webapi

import org.mulesoft.als.common.YPartBranch

trait IsInsideRequired {
  def isInsideRequired(yPartBranch: YPartBranch): Boolean =
    (yPartBranch.parentEntryIs(
      "required"
    ) || ((yPartBranch.isValue || yPartBranch.isArray || yPartBranch.isInArray) && yPartBranch
      .parentEntryIs("required"))) || yPartBranch.parentEntryIs("properties")
}
