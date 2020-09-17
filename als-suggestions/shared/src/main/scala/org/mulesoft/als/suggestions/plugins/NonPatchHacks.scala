package org.mulesoft.als.suggestions.plugins

import org.mulesoft.als.common.YPartBranch

trait NonPatchHacks {
  protected def notValue(yPartBranch: YPartBranch): Boolean =
    jsonHackNotValue(yPartBranch) || nonPatchNotValue(yPartBranch)

  private def nonPatchNotValue(yPartBranch: YPartBranch) =
    !yPartBranch.isJson && !yPartBranch.isValue

  private def jsonHackNotValue(yPartBranch: YPartBranch) =
    yPartBranch.isJson && yPartBranch.isKey
}
