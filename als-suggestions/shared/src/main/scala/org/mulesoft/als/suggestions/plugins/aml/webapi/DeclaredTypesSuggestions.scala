package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.annotations.SynthesizedField
import amf.core.metamodel.domain.ShapeModel
import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.models.UnresolvedShape
import amf.plugins.domain.webapi.models.{Parameter, Payload}
import org.mulesoft.als.common.SemanticNamedElement.ElementNameExtractor
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.{AMLRamlStyleDeclarationsReferences, BooleanSuggestions}
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.yaml.model.YMapEntry

trait DeclaredTypesSuggestions extends BooleanSuggestions {

  def typeProperty: PropertyMapping

  protected def suggestAllTypes(params: AmlCompletionRequest,
                                declaredSuggestions: Seq[RawSuggestion]): Seq[RawSuggestion] = {
    val name = params.amfObject.elementIdentifier()
    params.yPartBranch.parent
      .collectFirst({ case e: YMapEntry => e })
      .flatMap(_.key.asScalar.map(_.text)) match {
      case Some("type") => declaredSuggestions
      // I need to force generic shape model search for default amf parsed types

      case Some(text)
          if name.contains(text) || params.amfObject
            .isInstanceOf[UnresolvedShape] || text == "body" ||
            (params.branchStack.headOption.exists(h =>
              (h.isInstanceOf[Parameter] || h.isInstanceOf[Payload]) && !isBoolean(h, text)) && name
              .contains("schema")) =>
        declaredSuggestions ++ typeProperty
          .enum()
          .map(v => v.value().toString)
          .map(RawSuggestion.apply(_, isAKey = false))
      case Some(text) if params.branchStack.headOption.exists(h => isBoolean(h, text)) =>
        booleanSuggestions
      case _ => Nil
    }
  }

  protected def getDeclaredSuggestions(params: AmlCompletionRequest,
                                       actualName: Option[String],
                                       iri: String): Seq[RawSuggestion] = {
    if (canUseDeclared(params))
      new AMLRamlStyleDeclarationsReferences(Seq(iri), params.prefix, params.declarationProvider, actualName).resolve()
    else Seq.empty
  }
  protected def extractIri(params: AmlCompletionRequest, a: AmfObject): String =
    if (a.annotations.contains(classOf[SynthesizedField]) || params.yPartBranch.isEmptyNode)
      ShapeModel.`type`.head.iri()
    else a.metaURIs.head

  // today the only case in which this applies is raml types on async2
  // if any other case appears, check if this is still a valid mean to know if one can use declarations from other specs
  protected def canUseDeclared(params: AmlCompletionRequest): Boolean =
    params.nodeDialect == params.actualDialect
}
