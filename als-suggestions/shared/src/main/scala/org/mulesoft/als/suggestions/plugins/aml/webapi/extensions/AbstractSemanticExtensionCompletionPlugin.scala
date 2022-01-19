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
          .flatMap(params.alsConfigurationState.semanticKeysFor)
          .map { an =>
            if (an._2) RawSuggestion.forKey(formatForFlavour(an._1), "extensions", mandatory = false)
            else RawSuggestion.forObject(formatForFlavour(an._1), "extensions")
          } else Nil
    )

  protected def formatForFlavour(an: String): String

  protected def isAnnotationFlavour(params: AmlCompletionRequest): Boolean
}
