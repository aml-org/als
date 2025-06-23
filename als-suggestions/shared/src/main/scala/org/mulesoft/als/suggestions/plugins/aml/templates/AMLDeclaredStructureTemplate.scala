package org.mulesoft.als.suggestions.plugins.aml.templates

import org.mulesoft.als.configuration.TemplateTypes
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequest, DialectNodeFinder}
import org.mulesoft.amfintegration.AmfImplicits.DialectImplicits

/** templates for basic structure, based on the dialect model, no special consideration
  */
object AMLDeclaredStructureTemplate {
  def resolve(params: AmlCompletionRequest): Seq[RawSuggestion] =
    if (
      params.configurationReader.getTemplateType != TemplateTypes.NONE &&
      params.astPartBranch.isKey &&
      TemplateTools.isInsideDeclaration(params)
    ) {
      val decKey = params.nodeDocumentDefinition.declarationsMapTerms
        .find(t => params.astPartBranch.parentKey.contains(t._2))
        .map(_._1)
      val nm           = decKey.flatMap(DialectNodeFinder.find(_, params.nodeDocumentDefinition))
      val usedMappings = nm.map(_.propertiesMapping()).getOrElse(Seq.empty).filter(_.minCount().value() > 0)
      usedMappings.flatMap(TemplateTools.getFirstLevelTemplate(_, params)) match {
        case Nil => Seq.empty
        case children =>
          Seq(TemplateTools.firstTemplateSuggestionRaw(params, children))
      }
    } else Seq.empty
}
