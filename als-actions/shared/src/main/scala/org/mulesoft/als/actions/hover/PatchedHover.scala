package org.mulesoft.als.actions.hover

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.ClassTerm
import org.mulesoft.common.client.lexical.{PositionRange => AmfPositionRange}
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.annotations.Aliases
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.mulesoft.amfintegration.vocabularies.AlsPatchedVocabulary
import org.mulesoft.amfintegration.vocabularies.integration.{AlsVocabularyRegistry, VocabularyProvider}
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml10.Raml10UsesKeyTerm
import org.yaml.model.YMapEntry

class PatchedHover private (provider: VocabularyProvider) {

  private val dialectNames: Map[String, String] =
    Map(
      Raml10TypesDialect.dialect.id -> "raml10",
      Raml08TypesDialect.dialect.id -> "raml08",
      OAS20Dialect.dialect.id       -> "oas2",
      OAS30Dialect.dialect.id       -> "oas3",
      AsyncApi20Dialect.dialect.id  -> "asyncapi2",
      AsyncApi26Dialect.dialect.id  -> "asyncapi2"
    )

  def getHover(
      obj: AmfObject,
      branch: ASTPartBranch,
      dialect: Dialect
  ): Option[(Seq[String], Option[AmfPositionRange])] =
    obj.metaURIs.headOption.flatMap(metaUri => {
      branch.closestEntry match {
        case Some(entry: YMapEntry) => getPatchedHover(metaUri, entry, dialect.id)
        case _                      => None
      }

    })

  private def getPatchedHover(
      metaUri: String,
      entry: YMapEntry,
      dialectId: String
  ): Option[(Seq[String], Option[AmfPositionRange])] = {
    val dialectName = dialectNames.getOrElse(dialectId, "unknown")
    val valueType   = buildTerm(ValueType(metaUri), entry.key.toString, dialectName)
    provider
      .getDescription(valueType)
      .map(description => {
        (Seq(description), Some(entry.range))
      })
  }

  private def buildTerm(meta: ValueType, key: String, dialectName: String): ValueType =
    ValueType(AlsPatchedVocabulary.base + s"$dialectName/${meta.name}/$key")

  private def indexAliasTerms(aliases: Aliases, dialectName: String): VocabularyProvider = {
    val generatedTerms: Seq[ClassTerm] = aliases.aliases
      .map(alias => {
        val aliasTerm = buildAliasTerm(dialectName, alias._1)
        ClassTerm()
          .withName(aliasTerm)
          .withDisplayName(aliasTerm)
          .withDescription(Raml10UsesKeyTerm.description)
      })
      .toSeq
    val hoverProvider = provider.asInstanceOf[AlsVocabularyRegistry]
    hoverProvider.index(AlsPatchedVocabulary.base, generatedTerms)
    hoverProvider
  }

  private def buildAliasTerm(dialectName: String, name: String): String =
    s"$dialectName/WebAPI/$name"

  private def patches(dialectName: String, aliases: Aliases): Unit = {
    indexAliasTerms(aliases, dialectName)
  }

  private def init(dialect: Dialect, aliases: Aliases): Unit = {
    patches(dialectNames.getOrElse(dialect.id, "unknown"), aliases)
  }

}

object PatchedHover {

  def apply(provider: VocabularyProvider, dialectTerms: Seq[DialectTerms]): PatchedHover = {
    val ph = new PatchedHover(provider)
    provider match {
      case _: AlsVocabularyRegistry =>
        dialectTerms.foreach(t => {
          t.bu.annotations
            .find(classOf[Aliases])
            .foreach(aliases => ph.init(t.definedBy, aliases))
        })
      case _ =>
    }
    ph
  }
}
