package org.mulesoft.als.suggestions.plugins.aml

import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{PlainText, RawSuggestion, SuggestionStructure}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AMLRefTagCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLRefTagCompletionPlugin"

  private val includeSuggestion = Seq(
    RawSuggestion("!include ", "!include", "inclusion tag", Seq(), options = SuggestionStructure(rangeKind = PlainText))
  )
  val refSuggestion = Seq(
    RawSuggestion(
      "$ref",
      "$ref",
      "reference tag",
      Seq(),
      options = SuggestionStructure(isKey = true, isTopLevel = true)
    )
  )

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      getSuggestion(request, Option(request.nodeDialect.documents()).flatMap(_.referenceStyle().option()))
    }

  private def isDeclarable(params: AmlCompletionRequest): Boolean =
    isObjectDeclarable(params) && !params.amfObject.elementIdentifier().contains(params.astPartBranch.stringValue)

  protected def isObjectDeclarable(params: AmlCompletionRequest): Boolean =
    params.amfObject.metaURIs
      .exists(v => params.declarationProvider.isTermDeclarable(v))

  def getSuggestion(params: AmlCompletionRequest, style: Option[String]): Seq[RawSuggestion] =
    style match {
      case Some(ReferenceStyles.RAML) if isRamlTag(params)       => includeSuggestion
      case Some(ReferenceStyles.JSONSCHEMA) if isJsonKey(params) => refSuggestion
      case None if isJsonKey(params)                             => refSuggestion
      case _                                                     => Nil
    }

  def isRamlTag(params: AmlCompletionRequest): Boolean =
    params.astPartBranch.isValue && params.prefix.startsWith("!")

  def isJsonKey(params: AmlCompletionRequest): Boolean = params.astPartBranch match {
    case yPartBranch: YPartBranch =>
      (!yPartBranch.hasIncludeTag) && yPartBranch.brothers.isEmpty &&
      isDeclarable(params) &&
      isInFacet(params) &&
      matchPrefixPatched(params) &&
      !isExceptionCase(yPartBranch)
    case _ => false
  }

  private def matchPrefixPatched(params: AmlCompletionRequest) =
    params.astPartBranch.stringValue.isEmpty ||
      isEmptyJsonValue(params) ||
      params.astPartBranch.isArray ||
      params.astPartBranch.stringValue.startsWith("$")

  private def isEmptyJsonValue(params: AmlCompletionRequest) =
    (params.actualDialect.id.equals(AsyncApi20Dialect.DialectLocation) ||
      params.actualDialect.id.equals(AsyncApi26Dialect.DialectLocation)) &&
      params.baseUnit.id.endsWith(".json") &&
      params.astPartBranch.stringValue.equals("{}")

  private def isInFacet(params: AmlCompletionRequest): Boolean = isKeyAlone(params)

  private def isKeyAlone(params: AmlCompletionRequest): Boolean =
    params.fieldEntry.isEmpty && (params.astPartBranch.isKey || params.astPartBranch.isInArray)

  protected def isExceptionCase(branch: YPartBranch): Boolean = false
}

object AMLRefTagCompletionPlugin extends AMLRefTagCompletionPlugin
