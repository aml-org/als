package org.mulesoft.als.actions.hover

import amf.core.model.domain.AmfObject
import amf.core.parser
import amf.core.vocabulary.ValueType
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.amfintegration.{ALSAMLPlugin, SemanticDescriptionProvider}
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.yaml.model.{YMapEntry, YNodePlain, YValue}

class PatchedHover(provider: SemanticDescriptionProvider) {

  def getHover(obj: AmfObject, branch: YPartBranch, dialect: Dialect): Option[(Seq[String], Option[parser.Range])] =
    obj.metaURIs.headOption.flatMap(metaUri => {
      branch.parentEntry match {
        case Some(entry: YMapEntry) => getPatchedHover(metaUri, entry, dialect.id)
        case _                      => None
      }
    })

  private def getPatchedHover(metaUri: String,
                              entry: YMapEntry,
                              dialectId: String): Option[(Seq[String], Option[parser.Range])] = {
    val dialectName = dialectNames.getOrElse(dialectId, "unknown")
    provider
      .getSemanticDescription(buildTerm(ValueType(metaUri), entry.key.toString, dialectName))
      .map(description => {
        (Seq(description), Some(parser.Range(entry.range)))
      })
  }

  private def buildTerm(meta: ValueType, key: String, dialectName: String): ValueType =
    ValueType(s"http://als.patched/#${dialectName}/${meta.name}/${key}")

  private val dialectNames: Map[String, String] =
    Map(
      Raml10TypesDialect.dialect.id -> "raml10",
      Raml08TypesDialect.dialect.id -> "raml08",
      OAS20Dialect.dialect.id       -> "oas2",
      OAS30Dialect.dialect.id       -> "oas3",
      AsyncApi20Dialect.dialect.id  -> "asyncapi2"
    )

}
