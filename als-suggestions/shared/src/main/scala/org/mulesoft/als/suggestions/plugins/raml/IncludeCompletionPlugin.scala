package org.mulesoft.als.suggestions.plugins.raml

import amf.core.remote.{Raml08, Raml10, Vendor}
import org.mulesoft.als.suggestions.interfaces._
import org.yaml.model.{YNode, YScalar}

class IncludeCompletionPlugin extends InclusionSuggestion {
  override def id: String = IncludeCompletionPlugin.ID

  override def languages: Seq[Vendor] = IncludeCompletionPlugin.supportedLanguages

  override protected def decorate(path: String, prefix: String): String =
    if (path.startsWith("/") && prefix.replace("!include", "").trim == "/") path.stripPrefix("/") else path

  override protected def isRightTypeInclusion(request: ICompletionRequest): Boolean =
    if (request.actualYamlLocation.isEmpty)
      false
    else if (request.actualYamlLocation.get.node.isEmpty)
      false
    else if (!request.actualYamlLocation.get.node.get.yPart.isInstanceOf[YNode])
      false
    else {
      if (request.actualYamlLocation.get.value.isEmpty)
        false
      else if (!request.actualYamlLocation.get.value.get.yPart.isInstanceOf[YScalar])
        false
      else {
        val nodePart = request.actualYamlLocation.get.node.get.yPart

        val valuePart = request.actualYamlLocation.get.value.get.yPart.asInstanceOf[YScalar]

        val tagText = nodePart match {
          case node: YNode.MutRef => node.origTag.text

          case _: YNode => nodePart.tag.text

          case _ => ""
        }

        val valueString = Option(valuePart.value).map(_.toString).getOrElse("")

        if (tagText != "!include" && !valueString.startsWith("!include")) false
        else !UsesCompletionPlugin().isApplicable(request)
      }
    }

  override protected val description: String = "File path"
}

object IncludeCompletionPlugin {
  val ID = "include.completion"

  val supportedLanguages: List[Vendor] = List(Raml08, Raml10)

  def apply(): IncludeCompletionPlugin = new IncludeCompletionPlugin()
}
