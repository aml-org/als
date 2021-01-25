package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.annotations.SynthesizedField
import amf.core.metamodel.domain.ShapeModel
import amf.core.model.domain.Shape
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlParametersCompletionPlugin extends AMLCompletionPlugin with DeclaredTypesSuggestions {

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      params.amfObject match {
        case s: Parameter if params.yPartBranch.isValue =>
          val iri =
            extractIri(params, s)
          val declaredSuggestions =
            getDeclaredSuggestions(params, None, iri)
          suggestAllTypes(params, declaredSuggestions)
        case _ => Nil
      }
    }
  }

  override def typeProperty: PropertyMapping = Raml10TypesDialect.shapeTypesProperty

  override def id: String = "RamlParametersCompletionPlugin"
}
