package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.apicontract.client.scala.model.domain.Callback
import amf.apicontract.internal.metamodel.domain.{CallbackModel, EndPointModel, TemplatedLinkModel}
import amf.apicontract.internal.validation.runtimeexpression.{OAS3RuntimeExpressionParser, RuntimeExpressionParser}
import amf.core.internal.metamodel.Field
import amf.shapes.client.scala.model.domain.NodeShape
import amf.shapes.internal.domain.metamodel.IriTemplateMappingModel
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.AbstractRuntimeExpressionsCompletionPlugin

object OasRuntimeExpressionsCompletionPlugin extends AbstractRuntimeExpressionsCompletionPlugin {

  override protected def isApplicable(request: AmlCompletionRequest): Boolean =
    super.isApplicable(request) && request.prefix.contains("{")

  override protected def getSuffixForExpression(v: String, nonExpressionPrefix: String): String =
    if (!v.stripPrefix(nonExpressionPrefix).contains("}")) "}" else ""

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
    if (request.astPartBranch.isKey)
      request.branchStack.headOption match {
        case Some(c: Callback) =>
          request.astPartBranch.stringValue == c.expression.value()
        case _ => false
      }
    else request.amfObject.fields.fields().exists(fe => applicableFields.contains(fe.field))
  }

  override def parserObject(value: String): RuntimeExpressionParser = OAS3RuntimeExpressionParser(value)
}
