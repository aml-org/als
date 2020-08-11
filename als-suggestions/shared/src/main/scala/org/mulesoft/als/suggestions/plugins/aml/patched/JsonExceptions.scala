package org.mulesoft.als.suggestions.plugins.aml.patched

import org.mulesoft.als.common.YPartBranch

/*
 temp object to group all exception cases from json patching logic. Delete this when adopt json recovery
 */
object JsonExceptions {

  object SecuredBy {
    def isJsonException(yPart: YPartBranch) = yPart.isJson && yPart.isInArray
  }
}
