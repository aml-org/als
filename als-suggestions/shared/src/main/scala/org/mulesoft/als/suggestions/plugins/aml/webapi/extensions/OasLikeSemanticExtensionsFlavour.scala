package org.mulesoft.als.suggestions.plugins.aml.webapi.extensions

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

object OasLikeSemanticExtensionsFlavour extends AbstractSemanticExtensionCompletionPlugin {
  override protected def formatForFlavour(an: String): String =
    s"x-$an"

  override protected def isAnnotationFlavour(params: AmlCompletionRequest): Boolean =
    params.prefix.startsWith("x-")
}
