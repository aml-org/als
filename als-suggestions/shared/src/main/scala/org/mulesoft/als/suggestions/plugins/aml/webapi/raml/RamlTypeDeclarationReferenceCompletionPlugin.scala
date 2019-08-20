package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.annotations.SynthesizedField
import amf.core.metamodel.domain.ShapeModel
import amf.core.model.domain.Shape
import amf.plugins.domain.shapes.models.UnresolvedShape
import org.mulesoft.als.common.ElementNameExtractor._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLRamlStyleDeclarationsReferences
import org.yaml.model.YMapEntry
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RamlTypeDeclarationReferenceCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RamlTypeDeclarationReferenceCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      params.amfObject match {
        case s: Shape if params.yPartBranch.isValue =>
          val iri =
            if (s.annotations.contains(classOf[SynthesizedField]) || params.yPartBranch.isEmptyNode)
              ShapeModel.`type`.head.iri()
            else s.meta.`type`.head.iri()
          val declaredSuggestions = new AMLRamlStyleDeclarationsReferences(Seq(iri),
                                                                           params.prefix,
                                                                           params.declarationProvider,
                                                                           s.name.option()).resolve()

          val name = params.amfObject.elementIdentifier()
          params.yPartBranch.parent
            .collectFirst({ case e: YMapEntry => e })
            .flatMap(_.key.asScalar.map(_.text)) match {
            case Some("type") => declaredSuggestions
            // i need to force generic shape model search for default amf parsed types

            case Some(text)
                if name.exists(Seq(text, "schema").contains) || params.amfObject
                  .isInstanceOf[UnresolvedShape] || text == "body" =>
              declaredSuggestions ++ Raml10TypesDialect.shapeTypesProperty
                .enum()
                .map(v => v.value().toString)
                .map(RawSuggestion.apply(_, isAKey = false))
            case _ => Nil
          }
        case _ => Nil
      }
    }
  }

}
