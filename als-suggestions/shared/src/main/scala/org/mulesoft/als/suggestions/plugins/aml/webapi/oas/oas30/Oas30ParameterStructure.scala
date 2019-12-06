package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.annotations.LexicalInformation
import amf.core.model.StrField
import amf.core.parser.{Value, Position => AmfPosition}
import amf.dialects.oas.nodes.{Oas30AMLHeaderObject, Oas30ParamObject}
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.{Operation, Parameter}
import org.mulesoft.als.common.AmfSonElementFinder.AlsLexicalInformation
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object Oas30ParameterStructure extends AMLCompletionPlugin {
  override def id: String = "ParameterStructure"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case p: Parameter if isWrittinFacet(p, request.yPartBranch) => plainParam(p.binding)
        case r: Operation                                           => requestSuggestion(r, request.position)
        case _                                                      => Nil
      }
    }
  }

  private def requestSuggestion(op: Operation, position: Position): Seq[RawSuggestion] =
    operationParam(position.toAmfPosition, op).map(_.binding).map(plainParam).getOrElse(Nil)

  private def composeParams(op: Operation): Seq[Parameter] =
    Option(op.request)
      .map(r => r.cookieParameters ++ r.queryParameters ++ r.uriParameters ++ r.headers)
      .getOrElse(Nil)

  private def operationParam(amfPosition: AmfPosition, op: Operation): Option[Parameter] =
    composeParams(op).find(_.position().exists(_.contains(amfPosition)))

  private def plainParam(binding: StrField): Seq[RawSuggestion] =
    if (binding.option().contains("header")) headerProps else paramProps

  private lazy val paramProps = Oas30ParamObject.Obj.propertiesRaw()

  private lazy val headerProps = Oas30AMLHeaderObject.Obj.propertiesRaw()

  private def isWrittinFacet(p: Parameter, yPartBranch: YPartBranch) =
    p.name.value() != yPartBranch.stringValue && yPartBranch.isKey

  // hack case when param is under operation at ast but amf mapping that obj into request.
  private def isWritingParamInRequest(op: Operation, position: Position) = {
    op.fields.getValueAsOption(OperationModel.Request) exists {
      case Value(value, ann) =>
        ann.find(classOf[LexicalInformation]).exists(_.contains(position.toAmfPosition))
      case _ => false
    }
  }
}
