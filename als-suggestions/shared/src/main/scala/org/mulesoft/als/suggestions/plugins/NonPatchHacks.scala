package org.mulesoft.als.suggestions.plugins

import org.mulesoft.als.common.YPartBranch

trait NonPatchHacks {
  protected def notValue(yPartBranch: YPartBranch): Boolean =
    jsonHackNotValue(yPartBranch) || nonPatchNotValue(yPartBranch)

  protected def jsonPatchHack(yPartBranch: YPartBranch): Boolean =
    yPartBranch.stringValue == "x" && yPartBranch.isJson

  private def nonPatchNotValue(yPartBranch: YPartBranch) =
    !yPartBranch.isJson && (yPartBranch.isKey || yPartBranch.isInArray)

  private def jsonHackNotValue(yPartBranch: YPartBranch) =
    yPartBranch.isJson && yPartBranch.isKey
}
