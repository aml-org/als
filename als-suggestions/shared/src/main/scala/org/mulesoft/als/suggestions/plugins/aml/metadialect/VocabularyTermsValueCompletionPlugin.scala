package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.document.{Dialect, Vocabulary}
import amf.aml.client.scala.model.domain._
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.annotations.Aliases.Alias
import amf.core.internal.annotations.{Aliases, ReferencedInfo}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object VocabularyTermsValueCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "VocabularyTermsValueCompletionPlugin"

  def applies(amfObject: AmfObject, yPartBranch: YPartBranch): Boolean =
    isTerm(amfObject, yPartBranch) && yPartBranch.stringValue.contains(".")

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (applies(request.amfObject, request.yPartBranch))
      Future { suggest(request) } else
      emptySuggestion

  private def suggest(request: AmlCompletionRequest): Seq[RawSuggestion] = {
    (for {
      alias      <- request.yPartBranch.stringValue.split("\\.").headOption
      dialect    <- request.branchStack.collectFirst({ case d: Dialect => d })
      base       <- resolveAlias(dialect, alias).map(_._2.fullUrl)
      vocabulary <- findVocabulary(dialect, base)
    } yield {
      request.amfObject match {
        case _: NodeMapping => suggestClassTerms(vocabulary, alias)
        case _: PropertyLikeMapping[_] =>
          suggestPropertyTerms(request.branchStack, vocabulary, alias)
        case _ => Seq.empty
      }
    }).getOrElse(Seq.empty)
  }

  private def suggestPropertyTerms(branchStack: Seq[AmfObject],
                                   vocabulary: Vocabulary,
                                   alias: Alias): Seq[RawSuggestion] = {
    val usedPropertyTerms: Seq[String] = branchStack
      .collectFirst({
        case nm: NodeMapping => nm.propertiesMapping().flatMap(_.nodePropertyMapping().option())
      })
      .getOrElse(Seq.empty)
    vocabulary.declares.collect({
      case propertyTerm: PropertyTerm if !usedPropertyTerms.contains(propertyTerm.id) =>
        RawSuggestion.plain(
          addPrefix(propertyTerm.name.value(), alias),
          propertyTerm.displayName.option().getOrElse(propertyTerm.description.option().getOrElse("Property term")))
    })
  }

  private def suggestClassTerms(vocabulary: Vocabulary, alias: Alias): Seq[RawSuggestion] = {
    vocabulary.declares.collect({
      case classTerm: ClassTerm =>
        RawSuggestion.plain(
          addPrefix(classTerm.name.value(), alias),
          classTerm.displayName.option().getOrElse(classTerm.description.option().getOrElse("Class term")))
    })
  }

  private def findVocabulary(dialect: Dialect, base: String): Option[Vocabulary] = {
    dialect.references.collectFirst({
      case v: Vocabulary if v.base.value().contains(base) => v
    })
  }

  private def resolveAlias(dialect: Dialect, alias: Alias): Option[(Alias, ReferencedInfo)] =
    dialect.annotations
      .find(classOf[Aliases])
      .flatMap(aliases => {
        aliases.aliases.find(_._1 == alias)
      })

  def addPrefix(term: String, alias: Alias): String = s"$alias.$term"
}
