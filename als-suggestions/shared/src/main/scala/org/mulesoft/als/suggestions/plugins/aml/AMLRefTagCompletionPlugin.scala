package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.StrField
import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

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

  private def isDeclarable(amfObject: AmfObject, dialect: Dialect): Boolean = {
    val declared: Seq[String] = Option(dialect.documents())
      .map(documents => {
        val libDec: Seq[String] = Option(documents.library())
          .map(_.declaredNodes().flatMap(_.fields.fields()).map(_.value.toString))
          .getOrElse(Seq())
        val fragEnc: Seq[String] =
          Option(documents.fragments()).map(_.flatMap(_.fields.fields().map(_.value.toString))).getOrElse(Seq())
        val rootDec: Seq[String] = Option(documents.root())
          .map(_.declaredNodes().flatMap(_.fields.fields()).map(_.value.toString))
          .getOrElse(Seq())

        libDec ++ fragEnc ++ rootDec
      })
      .getOrElse(Seq())
    declared.exists(d => amfObject.meta.`type`.exists(_.iri() == d))
  }

  private def isValueRamlTag(params: AmlCompletionRequest) =
    params.yPartBranch.isValue && params.prefix.startsWith("!")

  private def isArrayTag(params: AmlCompletionRequest) =
    params.yPartBranch.brothers.nonEmpty || params.yPartBranch.isInArray ||
      !isDeclarable(params.amfObject, params.actualDialect)

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
    !(params.yPartBranch.hasIncludeTag || params.yPartBranch.brothers.nonEmpty ||
      params.yPartBranch.isInArray ||
      !isDeclarable(params.amfObject, params.actualDialect))
  }
}
