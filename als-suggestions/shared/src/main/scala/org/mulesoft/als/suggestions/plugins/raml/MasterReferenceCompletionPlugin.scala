package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.YScalar

class MasterReferenceCompletionPlugin extends InclusionSuggestion {
  override def id: String = MasterReferenceCompletionPlugin.ID

  override def languages: Seq[Vendor] = MasterReferenceCompletionPlugin.supportedLanguages

  def isExtendable(request: ICompletionRequest): Boolean = {
    request.astNode.nonEmpty && request.astNode.get.isElement && (request.astNode.get.asElement.get.definition
      .isAssignableFrom("Overlay") || request.astNode.get.asElement.get.definition.isAssignableFrom("Extension"))
  }

  def isInExtendsProperty(request: ICompletionRequest): Boolean = {
    if (request.actualYamlLocation.get == null) {
      return false
    }

    if (request.actualYamlLocation.get.keyValue.get == null) {
      return false
    }

    request.actualYamlLocation.get.keyValue.get.yPart.asInstanceOf[YScalar].text == "extends"
  }

  override protected val description: String = "master path"

  override protected def isRightTypeInclusion(request: ICompletionRequest): Boolean =
    isExtendable(request) && isInExtendsProperty(request)
}

object MasterReferenceCompletionPlugin {
  val ID = "masterRef.completion"

  val supportedLanguages: List[Vendor] = List(Raml10)

  def apply(): MasterReferenceCompletionPlugin = new MasterReferenceCompletionPlugin()
}
