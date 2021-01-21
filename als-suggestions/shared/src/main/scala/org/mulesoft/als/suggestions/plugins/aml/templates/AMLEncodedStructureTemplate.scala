package org.mulesoft.als.suggestions.plugins.aml.templates

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.configuration.TemplateTypes
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

/**
  * templates for basic structure, based on the dialect model, no special consideration
  */
object AMLEncodedStructureTemplate {
  def resolve(params: AmlCompletionRequest): Seq[RawSuggestion] = {
    val usedMappings = params.currentNode.map(_.propertiesMapping()).getOrElse(params.propertyMapping)
    TemplateTools.parentTermKey(params).flatMap(_.mapTermKeyProperty().option()) match {
      case Some(key) =>
        val parent = usedMappings.find(m => TemplateTools.iriForMapping(m) == key)
        val children =
          usedMappings.filterNot(m => TemplateTools.iriForMapping(m) == key).filter(_.minCount().value() > 0)
        (parent.map(_ => TemplateTools.firstTemplateSuggestionPM(params, children)).toSeq ++
          parent.map(_ => TemplateTools.fullTemplateSuggestionPM(params, children)).toSeq)
          .filter(_.children.nonEmpty)
      case None =>
        usedMappings.flatMap(pm => {
          templates(params, pm)
            .filter(rs => rs.children.nonEmpty) // if there is no child, then the snippet is not adding anything to the vanilla option
        })
    }
  }

  private def templates(params: AmlCompletionRequest, pm: PropertyMapping) = {
    val templateType = params.configurationReader.getTemplateType
    if (templateType == TemplateTypes.NONE) Seq.empty
    else
      TemplateTools.getFirstLevelTemplate(pm, params) ++ {
        if (templateType == TemplateTypes.FULL)
          TemplateTools.getFullTemplate(pm, params)
        else Seq.empty
      }
  }
}
