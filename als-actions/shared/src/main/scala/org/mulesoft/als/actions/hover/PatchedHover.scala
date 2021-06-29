package org.mulesoft.als.actions.hover

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.common.position.{Range => AmfRange}
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.ValueType
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.mulesoft.amfintegration.vocabularies.integration.VocabularyProvider
import org.yaml.model.YMapEntry

class PatchedHover(provider: VocabularyProvider) {

  def getHover(obj: AmfObject, branch: YPartBranch, dialect: Dialect): Option[(Seq[String], Option[AmfRange])] =
    obj.metaURIs.headOption.flatMap(metaUri => {
      branch.parentEntry match {
        case Some(entry: YMapEntry) => getPatchedHover(metaUri, entry, dialect.id)
        case _                      => None
      }
    })

  private def getPatchedHover(metaUri: String,
                              entry: YMapEntry,
                              dialectId: String): Option[(Seq[String], Option[AmfRange])] = {
    val dialectName = dialectNames.getOrElse(dialectId, "unknown")
    val valueType   = buildTerm(ValueType(metaUri), entry.key.toString, dialectName)
    provider
      .getDescription(valueType)
      .map(description => {
        (Seq(description), Some(AmfRange(entry.range)))
      })
  }

  private def buildTerm(meta: ValueType, key: String, dialectName: String): ValueType =
    ValueType(s"http://als.patched/#$dialectName/${meta.name}/$key")

  private val dialectNames: Map[String, String] =
    Map(
      Raml10TypesDialect.dialect.id -> "raml10",
      Raml08TypesDialect.dialect.id -> "raml08",
      OAS20Dialect.dialect.id       -> "oas2",
      OAS30Dialect.dialect.id       -> "oas3",
      AsyncApi20Dialect.dialect.id  -> "asyncapi2"
    )

}
