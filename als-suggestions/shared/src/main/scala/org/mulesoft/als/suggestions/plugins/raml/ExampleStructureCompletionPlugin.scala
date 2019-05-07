package org.mulesoft.als.suggestions.plugins.raml
import amf.core.parser.YNodeLikeOps
import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces.LocationKind.KEY_COMPLETION
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.high.level.interfaces.{IAttribute, IHighLevelNode, IParseResult}
import org.mulesoft.positioning.YamlLocation
import org.mulesoft.typesystem.nominal_interfaces.extras.{DescriptionExtra, PropertySyntaxExtra}
import org.yaml.model.{YMap, YMapEntry}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RequestOps {
  implicit class AstProviderOps(astProvider: IASTProvider) {
    def isYaml: Boolean = astProvider.syntax == Syntax.YAML
  }

}
class ExampleStructureCompletionPlugin extends ICompletionPlugin with ExampleCompletionTools {
  override def id: String = ExampleStructureCompletionPlugin.ID

  override def languages: Seq[Vendor] = ExampleStructureCompletionPlugin.supportedLanguages

  def onlyExampleFacets(node: IAttribute, facets: Seq[String], prefix: String): Boolean = {
    val p         = node.parent
    val allFacets = facets :+ (prefix + "k")
    p.flatMap(_.sourceInfo.yamlSources.headOption) match {
      case Some(y: YMapEntry) =>
        y.value.toOption[YMap] match {
          case Some(yMap) =>
            yMap.entries.forall(entry => allFacets.contains(entry.key.value.toString))
          case _ => false
        }
      case _ => false
    }
  }

  private def exampleFacets(p: Option[IHighLevelNode], isYaml: Boolean, prefix: String): Seq[Suggestion] = {
    p.map(_.definition.allProperties)
      .getOrElse(Seq())
      .filter(prop => !prop.getExtra(PropertySyntaxExtra).exists(extra => extra.isHiddenFromUI))
      .map(prop => {
        val pName       = prop.nameId.getOrElse("")
        val description = prop.getExtra(DescriptionExtra).map(_.text).getOrElse("")
        val text        = if (isYaml && pName.startsWith("$")) s""""$pName"""" else pName
        Suggestion(text, description, pName, prefix)
      })
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

    val suggestions =
      request.astNode.flatMap(_.asAttr) match {
        case Some(n) =>
          val possibleSuggestions = exampleFacets(n.parent, request.isYaml, request.prefix)
          if (valueExampleSpec(n) && onlyExampleFacets(n, possibleSuggestions.map(_.displayText), request.prefix))
            possibleSuggestions
          else
            Seq()

      }
    Future { CompletionResponse(filteredSuggestions(request, suggestions), KEY_COMPLETION, request) }
  }

  private def filteredSuggestions(request: ICompletionRequest, suggestions: Seq[Suggestion]) = {
    val siblings = request.astNode.map(existingSiblings).getOrElse(Seq())
    suggestions
      .filter(_.displayText.startsWith(request.prefix))
      .filter(s => !siblings.contains(s.displayText))
  }

  private def existingSiblings(node: IParseResult): Seq[String] =
    node.sourceInfo.yamlSources.headOption match {
      case Some(m: YMap) => m.entries.map(_.key.value.toString)
      case _             => Seq()
    }

  private def valueExampleSpec(n: IAttribute) =
    (n.name == "value") && n.parent.exists(_.definition.isAssignableFrom("ExampleSpec"))

  override def isApplicable(request: ICompletionRequest): Boolean =
    isStructureApplicable(request) && isExample(request)

  private def isStructureApplicable(request: ICompletionRequest) = {
    request.astNode match {
      case Some(node) =>
        request.actualYamlLocation.filter(_.inKey(request.position)) match {
          case Some(l) =>
            request.actualYamlLocation.exists(_.mapEntry.exists(x => x.yPart == l.mapEntry.get.yPart)) ||
              actualValueInMapEntries(request, l)
          case _ => false
        }
      case _ => false
    }
  }

  private def actualValueInMapEntries(request: ICompletionRequest, l: YamlLocation): Boolean = {
    request.yamlLocation.get.value.get.yPart match {
      case m: YMap =>
        l.mapEntry match {
          case Some(me) => m.entries.contains(me.yPart)
          case _        => false
        }
      case _ => false
    }
  }
}

object ExampleStructureCompletionPlugin {
  val ID = "example.structure.completion"

  val supportedLanguages: List[Vendor] = List(Raml10)

  def apply(): ExampleStructureCompletionPlugin = new ExampleStructureCompletionPlugin()
}
