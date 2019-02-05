package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.YScalar

class UsesCompletionPlugin extends InclusionSuggestion {

  override def id: String = UsesCompletionPlugin.ID

  override def languages: Seq[Vendor] = UsesCompletionPlugin.supportedLanguages

  override protected def isRightTypeInclusion(request: ICompletionRequest): Boolean = {
    request.astNode.isDefined && request.astNode.get.isElement &&
    request.astNode.get.asElement.get.definition.isAssignableFrom("LibraryBase") &&
    request.actualYamlLocation.isDefined &&
    request.actualYamlLocation.get.parentStack.length >= 3 &&
    request.actualYamlLocation.get.parentStack(2).keyValue.isDefined &&
    request.actualYamlLocation.get.parentStack(2).keyValue.get.yPart.isInstanceOf[YScalar] &&
    request.actualYamlLocation.get.parentStack(2).keyValue.get.yPart.asInstanceOf[YScalar].text == "uses" &&
    request.actualYamlLocation.get.value.isDefined &&
    request.actualYamlLocation.get.value.get.yPart.isInstanceOf[YScalar]

  }

  override protected val description: String = "RAML library path"
}

object UsesCompletionPlugin {
  val ID = "uses.completion"

  val supportedLanguages: List[Vendor] = List(Raml10)

  def apply(): UsesCompletionPlugin = new UsesCompletionPlugin()
}
