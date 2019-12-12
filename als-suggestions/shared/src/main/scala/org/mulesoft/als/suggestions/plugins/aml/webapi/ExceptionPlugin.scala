package org.mulesoft.als.suggestions.plugins.aml.webapi

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

trait ExceptionPlugin extends AMLCompletionPlugin {

  def applies(request: AmlCompletionRequest): Boolean
}
