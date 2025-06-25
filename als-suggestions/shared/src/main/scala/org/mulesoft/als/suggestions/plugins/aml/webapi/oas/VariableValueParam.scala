package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.apicontract.client.scala.model.domain.{Parameter, Server}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait VariableValueParam extends ExceptionPlugin {
  protected def isParamWithName(request: AmlCompletionRequest): Boolean = {
    request.amfObject match {
      case p: Parameter =>
        p.name.option().nonEmpty && request.fieldEntry.isEmpty && request.branchStack.headOption
          .exists(_.isInstanceOf[Server])
      case _ => false
    }
  }

  override def applies(request: AmlCompletionRequest): Boolean = isParamWithName(request)
  protected val variableDialectNode: DialectNode
  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (applies(request)) Future {
      variableDialectNode.Obj.propertiesRaw(fromDefinition = request.actualDocumentDefinition).filter(_.displayText != "name")
    }
    else
      emptySuggestion

  override def id: String = "VariableValueParam"
}
