package org.mulesoft.als.suggestions.plugins.aml.webapi.extensions

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

trait AbstractSemanticExtensionCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "SemanticExtensionsCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful(
      if (params.yPartBranch.isKey && !params.yPartBranch.isInArray && isAnnotationFlavour(params))
        params.amfObject.meta.`type`
          .map(_.iri())
          .flatMap(params.amfConfiguration.semanticKeysFor)
          .map(an => RawSuggestion.forKey(formatForFlavour(an), "extensions", mandatory = false))
      else Nil
    )

  protected def formatForFlavour(an: String): String =
    s"($an)"

  protected def isAnnotationFlavour(params: AmlCompletionRequest): Boolean =
    params.prefix.startsWith("(")
}
