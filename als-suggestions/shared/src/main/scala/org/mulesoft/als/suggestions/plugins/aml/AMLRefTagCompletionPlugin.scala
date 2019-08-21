package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.ReferenceStyles
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.common.ElementNameExtractor._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLRefTagCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLRefTagCompletionPlugin"

  private val includeSuggestion = Seq(
    RawSuggestion("!include ", "!include", "inclusion tag", Seq(), isKey = false, " "))
  private val refSuggestion = Seq(RawSuggestion("$ref", "$ref", "reference tag", Seq(), isKey = true, " "))

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future {
      getSuggestion(request, Option(request.actualDialect.documents()).flatMap(_.referenceStyle().option()))
    }

  private def isDeclarable(amfObject: AmfObject, dp: DeclarationProvider, actualKey: String): Boolean =
    amfObject.meta.`type`
      .exists(v => dp.isTermDeclarable(v.iri())) && !amfObject.elementIdentifier().contains(actualKey)

  private def isValueRamlTag(params: AmlCompletionRequest) =
    params.yPartBranch.isValue && params.prefix.startsWith("!")

  private def isArrayTag(params: AmlCompletionRequest) =
    params.yPartBranch.brothers.nonEmpty || params.yPartBranch.isInArray ||
      !isDeclarable(params.amfObject, params.declarationProvider, params.yPartBranch.stringValue)

  def getSuggestion(params: AmlCompletionRequest, style: Option[String]): Seq[RawSuggestion] = {
    style match {
      case Some(ReferenceStyles.RAML) if isRamlTag(params)       => includeSuggestion
      case Some(ReferenceStyles.JSONSCHEMA) if isJsonKey(params) => refSuggestion
      case None if isJsonKey(params)                             => refSuggestion
      case _                                                     => Nil
    }
  }

  def isRamlTag(params: AmlCompletionRequest): Boolean =
    params.yPartBranch.isValue && (params.prefix.startsWith("!") || params.yPartBranch.tag.exists(t => t.text == "!"))

  def isJsonKey(params: AmlCompletionRequest): Boolean = {
    !params.yPartBranch.hasIncludeTag && params.yPartBranch.brothers.isEmpty && isDeclarable(
      params.amfObject,
      params.declarationProvider,
      params.yPartBranch.stringValue) && params.fieldEntry.isEmpty && !params.yPartBranch.isInArray
  }
}
