package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.metamodel.Field
import amf.plugins.domain.webapi.metamodel.{CallbackModel, IriTemplateMappingModel, RequestModel, TemplatedLinkModel}
import amf.plugins.domain.webapi.models.Callback
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions.{
  InvalidExpressionToken,
  OASRuntimeExpressionParser,
  RuntimeExpressionParser
}
import org.mulesoft.amfmanager.dialect.DialectKnowledge

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object RuntimeExpressionsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RuntimeExpressionsCompletionPlugin"

  protected val applicableFields: Seq[Field] =
    Seq(CallbackModel.Expression, TemplatedLinkModel.RequestBody, IriTemplateMappingModel.LinkExpression)

  protected def parserObject(value: String): RuntimeExpressionParser = OASRuntimeExpressionParser(value)

  // TODO: LinkObject, Callbacks
  private def isApplicable(request: AmlCompletionRequest): Boolean =
    !(DialectKnowledge.isRamlInclusion(request.yPartBranch, request.actualDialect) ||
      DialectKnowledge.isJsonInclusion(request.yPartBranch, request.actualDialect)) &&
      appliesToField(request)

  private def appliesToField(request: AmlCompletionRequest): Boolean =
    request.fieldEntry match {
      case Some(fe) => applicableFields.contains(fe.field)
      case _ =>
        if (request.yPartBranch.isKey)
          request.branchStack.headOption match {
            case Some(c: Callback) =>
              request.yPartBranch.stringValue == c.expression.value() // ad-hoc for OAS 3 parser
            case _ => false
          } else request.amfObject.fields.fields().exists(fe => applicableFields.contains(fe.field))
    }

  // TODO: add navigation for fragments? Known values for tokens?
  private def extractExpression(v: String): Seq[RawSuggestion] = {
    val nonExpressionPrefix = if (v.contains("{")) v.substring(0, v.lastIndexOf("{") + 1) else ""
    val parser              = parserObject(v.stripPrefix(nonExpressionPrefix))
    (parser.completeStack.filterNot(_.isInstanceOf[InvalidExpressionToken]).lastOption match {
      case Some(other) => other.possibleApplications
      case None        => parser.possibleApplications
    }).map { s =>
      val pre = v.stripSuffix(
        parser.completeStack.collectFirst { case i: InvalidExpressionToken => i }.map(_.value).getOrElse(""))
      val displayText = pre.concat(s).stripPrefix(nonExpressionPrefix)
      RawSuggestion(s"$pre$s", displayText, isAKey = false, "RuntimeExpression", mandatory = false)
    }
  }

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (isApplicable(request))
      extractExpression(request.prefix)
    else Nil
  }
}
