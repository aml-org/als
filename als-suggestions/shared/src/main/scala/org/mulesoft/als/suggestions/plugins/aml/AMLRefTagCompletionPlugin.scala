package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.StrField
import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.ReferenceStyles
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AMLRefTagCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLRefTagCompletionPlugin"

  private val includeSuggestion = Seq(
    RawSuggestion("!include", "!include", "inclusion tag", Seq(), isKey = false, " "))
  private val refSuggestion = Seq(RawSuggestion("$ref", "$ref", "reference tag", Seq(), isKey = true, " "))

  override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] =
    Future {
      getSuggestion(params, Option(params.dialect.documents()).map(_.referenceStyle()))
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

  def getSuggestion(params: AMLCompletionParams, style: Option[StrField]): Seq[RawSuggestion] =
    if (params.yPartBranch.isValue)
      style match {
        case Some(s)
            if s.is(ReferenceStyles.JSONSCHEMA) ||
              params.yPartBranch.hasIncludeTag =>
          Seq()
        case _ => includeSuggestion
      } else
      style match {
        case Some(s)
            if s.is(ReferenceStyles.RAML) ||
              params.yPartBranch.brothers.nonEmpty ||
              params.yPartBranch.isInArray ||
              !isDeclarable(params.amfObject, params.dialect) =>
          Seq()
        case _ => refSuggestion
      }
}
