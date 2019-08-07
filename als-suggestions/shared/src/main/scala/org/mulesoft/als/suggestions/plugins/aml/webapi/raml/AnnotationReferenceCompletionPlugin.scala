package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

object AnnotationReferenceCompletionPlugin extends AMLCompletionPlugin {

  override def id: String = "AnnotationReferenceCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(
      if (params.yPartBranch.isKey && params.prefix.startsWith("(") || params.prefix.isEmpty) {
        params.declarationProvider
          .forNodeType(CustomDomainPropertyModel.`type`.head.iri())
          .map(an => RawSuggestion.forKey(s"($an)"))
          .toSeq
      } else Nil
    )
  }

}
