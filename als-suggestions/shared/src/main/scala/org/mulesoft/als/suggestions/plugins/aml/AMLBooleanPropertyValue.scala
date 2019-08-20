package org.mulesoft.als.suggestions.plugins.aml

import amf.core.vocabulary.Namespace.XsdTypes
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLBooleanPropertyValue extends AMLCompletionPlugin {
  override def id: String = "AMLBooleanPropertyValue"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.propertyMapping match {
        case head :: Nil if head.literalRange().option().contains(XsdTypes.xsdBoolean.iri()) =>
          Seq("true", "false").map(RawSuggestion(_, isAKey = false))
        case _ => Nil
      }
    }
  }
}
