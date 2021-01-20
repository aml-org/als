package org.mulesoft.als.suggestions.plugins.aml.templates

import org.mulesoft.als.configuration.TemplateTypes
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, DialectNodeFinder}
import org.mulesoft.amfintegration.AmfImplicits.DialectImplicits

/**
  * templates for basic structure, based on the dialect model, no special consideration
  */
object AMLDeclaredStructureTemplate {
  def resolve(params: AmlCompletionRequest): Seq[RawSuggestion] = {
    val templateType = params.configurationReader.getTemplateType
    if ((templateType == TemplateTypes.FULL) && params.yPartBranch.isKey && TemplateTools
          .isInsideDeclaration(params)) {
      val decKey = params.nodeDialect.declarationsMapTerms
        .find(t => params.yPartBranch.parentEntry.flatMap(_.key.asScalar).map(_.text).contains(t._2))
        .map(_._1)
      val nm           = decKey.flatMap(DialectNodeFinder.find(_, params.nodeDialect))
      val usedMappings = nm.map(_.propertiesMapping()).getOrElse(Seq.empty).filter(_.minCount().value() > 0)
      usedMappings.flatMap(TemplateTools.getFirstLevelTemplate(_, params)) match {
        case Nil => Seq.empty
        case children =>
          Seq(TemplateTools.firstTemplateSuggestion(children))
      }
    } else Seq.empty
  }
}
