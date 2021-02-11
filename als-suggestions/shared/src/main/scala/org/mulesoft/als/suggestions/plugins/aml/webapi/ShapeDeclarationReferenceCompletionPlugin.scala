package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.annotations.SynthesizedField
import amf.core.metamodel.domain.ShapeModel
import amf.core.model.domain.Shape
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.models.UnresolvedShape
import amf.plugins.domain.webapi.models.{Parameter, Payload}
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLRamlStyleDeclarationsReferences, BooleanSuggestions}
import org.yaml.model.YMapEntry
import org.mulesoft.amfintegration.AmfImplicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ShapeDeclarationReferenceCompletionPlugin extends AMLCompletionPlugin with DeclaredTypesSuggestions {

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      params.amfObject match {
        case s: Shape if params.yPartBranch.isValue =>
          val iri = extractIri(params, s)
          val declaredSuggestions =
            getDeclaredSuggestions(params, s.name.option(), iri)

          suggestAllTypes(params, declaredSuggestions)
        case _ => Nil
      }
    }
  }
}
