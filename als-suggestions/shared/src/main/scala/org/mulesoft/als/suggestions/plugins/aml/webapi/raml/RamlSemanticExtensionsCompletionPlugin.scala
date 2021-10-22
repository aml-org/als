package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.extensions.AbstractSemanticExtensionCompletionPlugin

object RamlSemanticExtensionsCompletionPlugin extends AbstractSemanticExtensionCompletionPlugin {
  override protected def formatForFlavour(an: String): String =
    s"($an)"

  override protected def isAnnotationFlavour(params: AmlCompletionRequest): Boolean =
    params.prefix.startsWith("(")
}
