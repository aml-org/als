package org.mulesoft.als.suggestions.plugins.aml

import amf.plugins.document.vocabularies.ReferenceStyles
import org.mulesoft.als.common.ElementNameExtractor._
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.{PlainText, RawSuggestion, SuggestionStructure}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfmanager.AmfImplicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AMLRefTagCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLRefTagCompletionPlugin"

  private val includeSuggestion = Seq(
    RawSuggestion("!include ",
                  "!include",
                  "inclusion tag",
                  Seq(),
                  options = SuggestionStructure(rangeKind = PlainText)))
  val refSuggestion = Seq(
    RawSuggestion("$ref",
                  "$ref",
                  "reference tag",
                  Seq(),
                  options = SuggestionStructure(isKey = true, isTopLevel = true)))

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      getSuggestion(request, Option(request.actualDialect.documents()).flatMap(_.referenceStyle().option()))
    }

  private def isDeclarable(params: AmlCompletionRequest): Boolean =
    isObjectDeclarable(params) && !params.amfObject.elementIdentifier().contains(params.yPartBranch.stringValue)

  protected def isObjectDeclarable(params: AmlCompletionRequest): Boolean = {
    params.amfObject.metaURIs
      .exists(v => params.declarationProvider.isTermDeclarable(v))
  }

  def getSuggestion(params: AmlCompletionRequest, style: Option[String]): Seq[RawSuggestion] = {
    style match {
      case Some(ReferenceStyles.RAML) if isRamlTag(params)       => includeSuggestion
      case Some(ReferenceStyles.JSONSCHEMA) if isJsonKey(params) => refSuggestion
      case None if isJsonKey(params)                             => refSuggestion
      case _                                                     => Nil
    }
  }

  def isRamlTag(params: AmlCompletionRequest): Boolean =
    params.yPartBranch.isValue && params.prefix.startsWith("!")

  def isJsonKey(params: AmlCompletionRequest): Boolean = {
    (!params.yPartBranch.hasIncludeTag) && params.yPartBranch.brothers.isEmpty &&
    isDeclarable(params) &&
    isInFacet(params) &&
    matchPrefixPatched(params) &&
    !isExceptionCase(params.yPartBranch)
  }

  private def matchPrefixPatched(params: AmlCompletionRequest) =
    params.yPartBranch.stringValue.isEmpty || isPatchedKey(params.yPartBranch) || params.yPartBranch.stringValue
      .startsWith("$")

  private def isInFacet(params: AmlCompletionRequest): Boolean = isKeyAlone(params) || isPatchedJson(params)

  private def isKeyAlone(params: AmlCompletionRequest): Boolean =
    params.fieldEntry.isEmpty && params.yPartBranch.isKey

  private def isPatchedJson(params: AmlCompletionRequest): Boolean =
    params.yPartBranch.isJson && params.yPartBranch.isInArray

  private def isPatchedKey(yPartBranch: YPartBranch): Boolean =
    (!yPartBranch.isJson && yPartBranch.stringValue == "k") || (yPartBranch.isJson && yPartBranch.stringValue == "x")

  protected def isExceptionCase(branch: YPartBranch): Boolean = false
}

object AMLRefTagCompletionPlugin extends AMLRefTagCompletionPlugin
