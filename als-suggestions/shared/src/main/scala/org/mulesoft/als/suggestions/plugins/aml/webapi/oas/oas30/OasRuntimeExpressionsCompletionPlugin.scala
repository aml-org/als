package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.{CallbackModel, EndPointModel, IriTemplateMappingModel, TemplatedLinkModel}
import amf.plugins.domain.webapi.models.Callback
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.AbstractRuntimeExpressionsCompletionPlugin
import amf.plugins.document.webapi.validation.runtimeexpression.{OAS3RuntimeExpressionParser, RuntimeExpressionParser}
import amf.plugins.domain.shapes.models.NodeShape

object OasRuntimeExpressionsCompletionPlugin extends AbstractRuntimeExpressionsCompletionPlugin {

  protected val applicableFields: Seq[Field] =
    Seq(CallbackModel.Expression, TemplatedLinkModel.RequestBody, IriTemplateMappingModel.LinkExpression)

  override protected def appliesToField(request: AmlCompletionRequest): Boolean = {
    request.fieldEntry match {
      case Some(fe) =>
        (applicableFields.contains(fe.field) && !request.branchStack.headOption.exists(_.isInstanceOf[NodeShape])) ||
          (fe.field == EndPointModel.Path && processByStack(request))
      case _ => processByStack(request)

    }
  }
  private def processByStack(request: AmlCompletionRequest) = {
    if (request.yPartBranch.isKey)
      request.branchStack.headOption match {
        case Some(c: Callback) =>
          request.yPartBranch.stringValue == c.expression.value()
        case _ => false
      } else request.amfObject.fields.fields().exists(fe => applicableFields.contains(fe.field))
  }

  override def parserObject(value: String): RuntimeExpressionParser = OAS3RuntimeExpressionParser(value)
}
