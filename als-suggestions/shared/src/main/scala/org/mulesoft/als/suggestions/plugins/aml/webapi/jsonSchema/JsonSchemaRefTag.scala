package org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.plugins.aml.AMLRefTagCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.IsInsideRequired

object JsonSchemaRefTag extends AMLRefTagCompletionPlugin with IsInsideRequired {

  override def isExceptionCase(branch: YPartBranch): Boolean = isInsideRequired(branch)
}
