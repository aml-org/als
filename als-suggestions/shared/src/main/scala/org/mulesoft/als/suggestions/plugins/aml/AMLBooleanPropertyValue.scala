package org.mulesoft.als.suggestions.plugins.aml

import amf.core.vocabulary.Namespace.XsdTypes
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLBooleanPropertyValue extends AMLCompletionPlugin with BooleanSuggestions {
  override def id: String = "AMLBooleanPropertyValue"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.propertyMapping match {
        case head :: Nil if head.literalRange().option().contains(XsdTypes.xsdBoolean.iri()) =>
          booleanSuggestions
        case _ => Nil
      }
    }
  }
}
