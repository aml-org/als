package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.templates.{ResourceTypeModel, TraitModel}
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.internal.parser.domain.FieldEntry
import amf.plugins.document.vocabularies.plugin.ReferenceStyles
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.aml.declarations.DeclarationProvider
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.common.client.lexical.ASTElement
import org.yaml.model.YMapEntry

import scala.concurrent.Future
class AMLRamlStyleDeclarationsReferences(
    nodeTypeMappings: Seq[String],
    prefix: String,
    provider: DeclarationProvider,
    actualName: Option[String]
) {

  def resolve(builder: (String, DomainElement) => RawSuggestion =
                (name: String, _: DomainElement) => RawSuggestion(name, isAKey = false)  // todo: proba sin el parametro default a ver cuales plugins llaman y hacen un copy
              // en principio ninguno deberia hacer copy, y en su lugar directamente armar bien el raw suggestion en el builder
             ): Seq[RawSuggestion] = {
    val values: Seq[(String, DomainElement)] =
      if (prefix.contains(".")) prefix.split('.').headOption.map(resolveAliased).getOrElse(Nil)
      else resolveLocal(actualName)

    values.map(t => builder(t._1, t._2))
  }

  private def resolveAliased(alias: String): Seq[(String, DomainElement)] =
    nodeTypeMappings
      .flatMap(provider.getElementByName(_, alias))
      .map(n => (alias + "." + n._1, n._2))

  private def resolveLocal(actualName: Option[String]): Seq[(String, DomainElement)] = {
    val names =
      nodeTypeMappings
        .map(np => provider.getElementByName(np, _))
    actualName.fold(names)(n => names.filter(_._1 != n))
  }
}

object AMLRamlStyleDeclarationsReferences extends AMLDeclarationReferences {
  override def id: String = "AMLRamlStyleDeclarationsReferences"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    Future.successful({
      if (params.astPartBranch.isValue && styleOrEmpty(params.actualDialect)) {
        val actualName = params.amfObject.elementIdentifier()
        new AMLRamlStyleDeclarationsReferences(
          getObjectRangeIds(params),
          params.prefix,
          params.declarationProvider,
          actualName
        ).resolve()
      } else Seq.empty
    })

  private def styleOrEmpty(dialect: Dialect) =
    dialect.documents().referenceStyle().option().forall(_ == ReferenceStyles.RAML)

}

trait AMLDeclarationReferences extends AMLCompletionPlugin {

  protected def getObjectRangeIds(params: AmlCompletionRequest): Seq[String] = {
    val candidates = getFieldIri(params.fieldEntry, params.propertyMapping)
      .orElse(declaredFromKey(params.astPartBranch.parent, params.propertyMapping))
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

  private def getFieldIri(
      fieldEntry: Option[FieldEntry],
      propertyMapping: Seq[PropertyMapping]
  ): Option[PropertyMapping] =
    fieldEntry.flatMap(fe => propertyMapping.find(_.nodePropertyMapping().value() == fe.field.value.iri()))

  private def declaredFromKey(
      parent: Option[ASTElement],
      propertyMapping: Seq[PropertyMapping]
  ): Option[PropertyMapping] =
    parent
      .collect({ case entry: YMapEntry => entry.key.toString })
      .flatMap(k => propertyMapping.find(p => p.name().option().contains(k)))
}
