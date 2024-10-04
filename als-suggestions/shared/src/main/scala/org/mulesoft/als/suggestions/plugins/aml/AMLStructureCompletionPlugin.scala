package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.model.document.Module
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.ArrayLike
import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.{AmfObjectKnowledge, DisjointCompletionPlugins, ResolveIfApplies}
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry
import org.mulesoft.als.suggestions.plugins.aml.templates.{AMLDeclaredStructureTemplate, AMLEncodedStructureTemplate}
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.Async2PayloadExampleMatcher
import org.mulesoft.amfintegration.AmfImplicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLStructureCompletionsPlugin(propertyMapping: Seq[PropertyMapping], d: Dialect) {
  def resolve(classTerm: String): Seq[RawSuggestion] =
    propertyMapping.map(p => p.toRaw(CategoryRegistry(classTerm, p.name().value(), d.id)))
}

case class StructureCompletionPlugin(resolvers: List[ResolveIfApplies]) extends DisjointCompletionPlugins {
  override final def id = "AMLStructureCompletionPlugin"
}

object ResolveDefault extends ResolveIfApplies with AmfObjectKnowledge with Async2PayloadExampleMatcher {
  override def resolve(params: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    applies(defaultStructure(params))

  protected final def defaultStructure(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (params.astPartBranch.isKeyLike && !isExampleAtPayload(params))
      if (!isInFieldValue(params)) {
        val isEncoded =
          isEncodes(params.amfObject, params.actualDialect, params.branchStack) && isEmptyFieldOrPrefix(
            params
          )
        val isEncodedOrModule = isEncoded || params.amfObject.isInstanceOf[Module]
        if (
          ((isEncodedOrModule && params.astPartBranch.isAtRoot) || !isEncodedOrModule)
          && isEmptyFieldOrPrefix(params)
        )
          new AMLStructureCompletionsPlugin(params.propertyMapping, params.actualDialect)
            .resolve(params.amfObject.metaURIs.head) ++
            AMLEncodedStructureTemplate.resolve(params)
        else
          AMLDeclaredStructureTemplate.resolve(params)
      } else
        resolveObjInArray(params)
    else Nil
  }

  private def isEmptyFieldOrPrefix(params: AmlCompletionRequest) =
    (params.prefix.isEmpty && params.fieldEntry.isEmpty) ||
      params.prefix.nonEmpty

  protected def objInArray(params: AmlCompletionRequest): Option[DomainElementModel] =
    params.fieldEntry match {
      case Some(FieldEntry(Field(t: ArrayLike, _, _, _, false, _), _))
          if t.element
            .isInstanceOf[DomainElementModel] && params.astPartBranch.isInArray =>
        Some(t.element.asInstanceOf[DomainElementModel])
      case _ => None
    }

  protected def resolveObjInArray(params: AmlCompletionRequest): Seq[RawSuggestion] = {
    objInArray(params) match {
      case Some(meta) =>
        val props = params.actualDialect.declares
          .collect({ case nm: NodeMapping => nm })
          .find(_.nodetypeMapping.value() == meta.`type`.head.iri())
          .map(_.propertiesMapping())
          .getOrElse(Nil)

        new AMLStructureCompletionsPlugin(props, params.actualDialect).resolve(meta.`type`.head.iri())
      case _ => Nil
    }
  }
}
