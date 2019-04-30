package org.mulesoft.als.suggestions.plugins.raml
import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.model.domain.AmfObject
import amf.core.parser.{Position => AmfPosition}
import amf.core.remote.{Raml10, Vendor}
import amf.plugins.document.webapi.model.NamedExampleFragment
import amf.plugins.domain.shapes.models.Example
import common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces.LocationKind.KEY_COMPLETION
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.als.suggestions.plugins.raml.ExampleStructureCompletionPlugin.{
  ID,
  possibleSuggestions,
  supportedLanguages
}
import org.yaml.model.{YMap, YMapEntry}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RequestOps {
  implicit class AstProviderOps(astProvider: IASTProvider) {
    def isYaml: Boolean = astProvider.syntax == Syntax.YAML
  }

}
class ExampleStructureCompletionPlugin extends ICompletionPlugin with ExampleCompletionTools {
  override def id: String = ID

  override def languages: Seq[Vendor] = supportedLanguages

  private def getExampleNode(amfOpt: Option[AmfObject], pos: Position): Option[Example] = {
    amfOpt match {
      case Some(ex: Example) => Some(ex)
      case Some(nef: NamedExampleFragment) =>
        nef.encodes.examples
          .find(
            e =>
              e.annotations
                .find(classOf[SourceAST])
                .exists(s =>
                  PositionRange(s.ast.range)
                    .contains(pos)))
      case _ => None
    }
  }

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {
    Future {
      val suggestions: Seq[String] =
        findExample(request)
          .map(t => possibleSuggestions.filter(s => !getAstFacets(t._2.toAmfPosition(), t._1).contains(s)))
          .getOrElse(Nil)

      CompletionResponse(suggestions.map(s => Suggestion(s, s, s, request.prefix)), KEY_COMPLETION, request)

    }
  }

  private def inName(ex: Example, amfPosition: AmfPosition): Boolean = {
    ex.name.annotations().find(classOf[LexicalInformation]).exists(_.range.start.line == amfPosition.line)
  }

  private def getAstFacets(position: AmfPosition, ex: Example): Seq[String] =
    getFacets(ex, position)

  private def findExample(request: ICompletionRequest): Option[(Example, Position)] = {
    val position = Position(request.position, request.config.originalContent.getOrElse(""))
    getExampleNode(request.astNode.map(_.amfNode), position).map(e => (e, position))
  }

  def getProperties(yMap: YMap, position: AmfPosition): Seq[String] =
    yMap.entries.filter(e => e.key.range.lineFrom != position.line).flatMap { e =>
      e.key.asScalar.map(_.text)
    }

  def getValueProperties(yMapEntry: YMapEntry, position: AmfPosition): Seq[String] =
    yMapEntry.value.asOption[YMap].map(getProperties(_, position)).getOrElse(Nil)

  def getFacets(amfObject: AmfObject, position: AmfPosition): Seq[String] =
    amfObject.annotations.find(classOf[SourceAST]).map(_.ast) match {
      case Some(map: YMap)    => getProperties(map, position) // anonymous example
      case Some(e: YMapEntry) => getValueProperties(e, position) // named example
      case _                  => Nil
    }

  private def onlyExampleFacets(ex: Example, position: Position): Boolean = {
    val amfPosition = position.toAmfPosition()
    !inName(ex, amfPosition) && getAstFacets(amfPosition, ex).forall(f => possibleSuggestions.contains(f))
  }

  override def isApplicable(request: ICompletionRequest): Boolean =
    isKey(request) && exampleQualifies(request)

  private def exampleQualifies(request: ICompletionRequest) = {
    findExample(request) match {
      case Some((ex, position)) => onlyExampleFacets(ex, position)
      case _                    => false
    }
  }

  private def isKey(request: ICompletionRequest) =
    request.actualYamlLocation.exists(_.inKey(request.position))

}

object ExampleStructureCompletionPlugin {
  val ID = "example.structure.completion"

  val supportedLanguages: List[Vendor] = List(Raml10)

  def apply(): ExampleStructureCompletionPlugin = new ExampleStructureCompletionPlugin()

  val possibleSuggestions: Seq[String] = Seq(
    "description",
    "value",
    "strict",
    "displayName",
    "annotations"
  )
}
