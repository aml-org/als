package org.mulesoft.als.suggestions.plugins.raml

import amf.core.annotations.SourceAST
import amf.core.model.document.Document
import amf.core.remote.{Raml08, Raml10, Vendor}
import amf.plugins.domain.webapi.models.{Parameter, WebApi}
import org.mulesoft.als.common.NodeBranchBuilder
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.implementation.{CompletionResponse, Suggestion}
import org.mulesoft.als.suggestions.interfaces.{
  ICompletionPlugin,
  ICompletionRequest,
  ICompletionResponse,
  LocationKind
}
import org.yaml.model.{YMapEntry, YPart}

import scala.concurrent.Future

class BaseUriParametersCompletionPlugin extends ICompletionPlugin {
  override def id: String = BaseUriParametersCompletionPlugin.ID

  override def languages: Seq[Vendor] =
    BaseUriParametersCompletionPlugin.supportedLanguages

  override def suggest(request: ICompletionRequest): Future[ICompletionResponse] = {

    def toSuggestions(result: Seq[String]): Seq[Suggestion] =
      result.map(r => Suggestion(s"$r", "Base URI parameter", r, request.prefix))

    def isCurrentNode(value: Parameter): Boolean =
      request.astNode.exists(_.amfNode.asInstanceOf[Parameter] == value)

    def alreadyDefined(value: Parameter, parent: Option[YPart]): Boolean =
      parent.map(_.children).getOrElse(Nil).exists {
        case entry: YMapEntry =>
          entry.key.asScalar.exists(scalar => scalar.toString == value.name.value())
        case _ => false
      }

    val parent: Option[YPart] =
      getYaml(request).flatMap(NodeBranchBuilder.build(_, getPosition(request)).parent)

    val result: Seq[String] = request.astNode
      .map(ast =>
        ast.amfBaseUnit match {
          case amf: Document =>
            amf.encodes match {
              case webApi: WebApi =>
                webApi.servers.flatMap(
                  _.variables
                    .filterNot(isCurrentNode)
                    .filterNot(alreadyDefined(_, parent))
                    .map(_.name.value()))
              case _ => Nil
            }
          case _ => Nil
      })
      .getOrElse(Nil)
      .filter(_.startsWith(request.prefix))

    Future.successful(CompletionResponse(toSuggestions(result), LocationKind.KEY_COMPLETION, request))
  }

  private def getPosition(request: ICompletionRequest): Position =
    Position(request.position, request.astNode.flatMap(_.amfBaseUnit.raw).getOrElse("")).moveLine(1)

  private def getYaml(request: ICompletionRequest): Option[YPart] =
    request.astNode.flatMap(ast =>
      ast.amfBaseUnit match {
        case amf: Document => amf.encodes.annotations.find(classOf[SourceAST]).map(_.ast)
        case _             => None
    })

  override def isApplicable(request: ICompletionRequest): Boolean =
    request.astNode.exists(_.amfNode.isInstanceOf[Parameter]) &&
      getYaml(request).forall(a => {
        val amfPosition = Position(request.position, request.astNode.flatMap(_.amfBaseUnit.raw).getOrElse(""))
          .moveLine(1)
        NodeBranchBuilder.build(a, amfPosition).isKey
      })
}

object BaseUriParametersCompletionPlugin {
  val ID                               = "base.uri.parameters.completion"
  val supportedLanguages: List[Vendor] = List(Raml08, Raml10)

  def apply(): BaseUriParametersCompletionPlugin =
    new BaseUriParametersCompletionPlugin()
}
