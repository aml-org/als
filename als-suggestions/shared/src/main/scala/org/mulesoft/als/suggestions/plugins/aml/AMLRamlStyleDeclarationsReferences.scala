package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.domain.DomainElementModel
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits._
import org.yaml.model.{YMapEntry, YPart}

import scala.concurrent.Future
class AMLRamlStyleDeclarationsReferences(nodeTypeMappings: Seq[String],
                                         prefix: String,
                                         provider: DeclarationProvider,
                                         actualName: Option[String]) {

  def resolve(): Seq[RawSuggestion] = {
    val values =
      if (prefix.contains(".")) prefix.split('.').headOption.map(resolveAliased).getOrElse(Nil)
      else resolveLocal(actualName)

    values.map(RawSuggestion.apply(_, isAKey = false))
  }

  private def resolveAliased(alias: String) =
    nodeTypeMappings
      .flatMap(provider.forNodeType(_, alias))
      .map(n => alias + "." + n)

  private def resolveLocal(actualName: Option[String]) = {
    val names = nodeTypeMappings.flatMap(np => provider.forNodeType(np))
    actualName.fold(names)(n => names.filter(_ != n))
  }
}

object AMLRamlStyleDeclarationsReferences extends AMLDeclarationReferences {
  override def id: String = "AMLRamlStyleDeclarationsReferences"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful({
      if (params.yPartBranch.isValue && styleOrEmpty(params.actualDialect)) {
        val actualName = params.amfObject.elementIdentifier()
        new AMLRamlStyleDeclarationsReferences(getObjectRangeIds(params),
                                               params.prefix,
                                               params.declarationProvider,
                                               actualName).resolve()
      } else Seq.empty
    })

  private def styleOrEmpty(dialect: Dialect) =
    dialect.documents().referenceStyle().option().forall(_ == ReferenceStyles.RAML)

}

trait AMLDeclarationReferences extends AMLCompletionPlugin {

  protected def getObjectRangeIds(params: AmlCompletionRequest): Seq[String] = {
    val candidates = getFieldIri(params.fieldEntry, params.propertyMapping)
      .orElse(declaredFromKey(params.yPartBranch.parent, params.propertyMapping))
      .map(_.objectRange().flatMap(_.option())) match {
      case Some(seq) => seq
      case _ =>
        params.amfObject.metaURIs.headOption.toSeq
    }
    candidates.filterNot(exceptions.contains)
  }

  protected val exceptions = Seq(
    DomainElementModel.`type`.head.iri(),
    // handled by RamlAbstractDeclarationReference:
    TraitModel.`type`.head.iri(),
    ResourceTypeModel.`type`.head.iri()
  )

  private def getFieldIri(fieldEntry: Option[FieldEntry],
                          propertyMapping: Seq[PropertyMapping]): Option[PropertyMapping] =
    fieldEntry.flatMap(fe => propertyMapping.find(_.nodePropertyMapping().value() == fe.field.value.iri()))

  private def declaredFromKey(parent: Option[YPart], propertyMapping: Seq[PropertyMapping]): Option[PropertyMapping] =
    parent
      .collect({ case entry: YMapEntry => entry.key.toString })
      .flatMap(k => propertyMapping.find(p => p.name().option().contains(k)))
}
