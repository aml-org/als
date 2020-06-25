package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.Field
import amf.core.metamodel.Type.ArrayLike
import amf.core.metamodel.domain.DomainElementModel
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.{AmfObjectKnowledge, DisjointCompletionPlugins, ResolveIfApplies}
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry
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

object ResolveDefault extends ResolveIfApplies with AmfObjectKnowledge {
  override def resolve(params: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    applies(defaultStructure(params))

  protected final def defaultStructure(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (isWritingProperty(params.yPartBranch))
      if (!isInFieldValue(params)) {
        val isEncoded = isEncodes(params.amfObject, params.actualDialect) && params.fieldEntry.isEmpty // params.fieldEntry.isEmpty does nothing here?
        if (((isEncoded && params.yPartBranch.isAtRoot) || !isEncoded) && params.fieldEntry.isEmpty)
          new AMLStructureCompletionsPlugin(params.propertyMapping, params.actualDialect)
            .resolve(params.amfObject.metaURIs.head)
        else Nil
      } else resolveObjInArray(params)
    else Nil
  }

  private def isWritingProperty(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKey || (yPartBranch.isJson && (yPartBranch.isInArray && yPartBranch.stringValue == "x"))

  protected def objInArray(params: AmlCompletionRequest): Option[DomainElementModel] = {
    params.fieldEntry match {
      case Some(FieldEntry(Field(t: ArrayLike, _, _, _), _))
          if t.element
            .isInstanceOf[DomainElementModel] && params.yPartBranch.isInArray =>
        Some(t.element.asInstanceOf[DomainElementModel])
      case _ => None
    }
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
