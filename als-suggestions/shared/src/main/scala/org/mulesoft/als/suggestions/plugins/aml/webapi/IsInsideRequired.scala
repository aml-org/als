package org.mulesoft.als.suggestions.plugins.aml.webapi

import org.mulesoft.als.common.ASTPartBranch

trait IsInsideRequired {
  def isInsideRequired(astPartBranch: ASTPartBranch): Boolean =
    (astPartBranch.parentEntryIs(
      "required"
    ) || ((astPartBranch.isValue || astPartBranch.isArray || astPartBranch.isInArray) && astPartBranch
      .parentEntryIs("required"))) || astPartBranch.parentEntryIs("properties")
}
