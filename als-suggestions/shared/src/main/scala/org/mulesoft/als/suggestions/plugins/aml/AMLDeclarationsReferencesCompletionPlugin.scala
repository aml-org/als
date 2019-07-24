package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.{AmfObject, AmfScalar}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.suggestions.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.yaml.model.{YMapEntry, YPart}

import scala.collection.immutable
import scala.concurrent.Future

class AMLDeclarationsReferencesCompletionPlugin(nodeTypeMappings: Seq[String],
                                                prefix: String,
                                                provider: DeclarationProvider,
                                                actualName: Option[String]) {

  def resolve(): Seq[RawSuggestion] = {
    val values =
      if (prefix.contains(".")) resolveAliased(prefix.split('.').head)
      else resolveLocal(actualName)

    values.map(RawSuggestion.apply(_, isAKey = false))
  }

  private def resolveAliased(alias: String) =
    nodeTypeMappings
      .flatMap(provider.forNodeType(_, alias))
      .map(n => alias + "." + n)

  private def resolveLocal(actualName: Option[String]) = {
    val names = nodeTypeMappings.flatMap(provider.forNodeType)
    actualName.fold(names)(n => names.filter(_ != n))
  }
}

object AMLDeclarationsReferencesCompletionPlugin extends CompletionPlugin {
  override def id: String = "AMLDeclarationsReferencesCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    Future.successful({
      if (params.yPartBranch.isValue) {
        val actualName = params.amfObject.fields
          .getValueAsOption(DialectDomainElementModel.DeclarationName)
          .map(_.value)
          .collect({ case s: AmfScalar => s.value.toString })
        new AMLDeclarationsReferencesCompletionPlugin(getObjectRangeIds(params),
                                                      params.prefix,
                                                      params.declarationProvider,
                                                      actualName).resolve()
      } else Seq.empty
    })

  }

  private def getObjectRangeIds(params: CompletionParams): Seq[String] = {
    getFieldIri(params.fieldEntry, params.propertyMappings)
      .orElse(declaredFromKey(params.yPartBranch.parent, params.propertyMappings))
      .map(_.objectRange().flatMap(_.option())) match {
      case Some(seq) => seq
      case _         => referenceFromDeclared(params.amfObject)
    }

  }
  private def getFieldIri(fieldEntry: Option[FieldEntry],
                          propertyMapping: Seq[PropertyMapping]): Option[PropertyMapping] =
    fieldEntry.flatMap(fe => propertyMapping.find(_.nodePropertyMapping().value() == fe.field.value.iri()))

  private def referenceFromDeclared(amfObject: AmfObject): immutable.Seq[String] = {
    amfObject.fields.fields() match {
      case head :: Nil if head.field == DialectDomainElementModel.DeclarationName =>
        amfObject.meta.`type`.map(_.iri())
      case _ => Nil
    }
  }
  private def declaredFromKey(parent: Option[YPart], propertyMapping: Seq[PropertyMapping]): Option[PropertyMapping] =
    parent
      .collect({ case entry: YMapEntry => entry.key.toString })
      .flatMap(k => propertyMapping.find(p => p.name().option().contains(k)))
}
