package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.SemanticExtension
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.yaml.model.YMapEntry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MetaDialectExtensionsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "MetaDialectExtensionsCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (applies(request)) Future {
      request.baseUnit match {
        case d: Dialect =>
          d.annotationMappings()
            .flatMap(_.name.option())
            .map(n => RawSuggestion(n, n, isAKey = false, "Annotation Mapping", mandatory = false))
        case _ => Seq.empty // should be inaccessible
      }
    } else emptySuggestion

  private def applies(request: AmlCompletionRequest): Boolean =
    request.yPartBranch.isValue &&
      (isOnlyExtensionHack(request) || request.amfObject.isInstanceOf[SemanticExtension])

  // TODO: remove when APIMF-3588 is done
  private def isOnlyExtensionHack(request: AmlCompletionRequest) = {
    request.amfObject.isInstanceOf[Dialect] &&
    request.yPartBranch.stack.size == 6 &&
    request.yPartBranch
      .getAncestor(3)
      .exists(p =>
        p match {
          case e: YMapEntry => e.key.asScalar.exists(_.text == "extensions")
          case _            => false
      })
  }
}
