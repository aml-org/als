package org.mulesoft.als.suggestions.plugins.aml

import amf.core.metamodel.Field
import amf.core.metamodel.Type.ArrayLike
import amf.core.metamodel.domain.DomainElementModel
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLStructureCompletionsPlugin(propertyMapping: Seq[PropertyMapping]) {
  def resolve(classTerm: String): Seq[RawSuggestion] =
    propertyMapping.map(p => p.toRaw(CategoryRegistry(classTerm, p.name().value())))

}

object AMLStructureCompletionPlugin extends StructureCompletionPlugin

// inside StructureCompletionPlugin?
trait ResolveIfApplies {
  def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]]

  protected val notApply: Option[Future[Seq[RawSuggestion]]] = None

  protected def applies(response: Future[Seq[RawSuggestion]]): Option[Future[Seq[RawSuggestion]]] =
    Some(response)
}

trait StructureCompletionPlugin extends AMLCompletionPlugin {
  override final def id = "AMLStructureCompletionPlugin"

  protected val resolvers: List[ResolveIfApplies] = List(
    ResolveDefault
  )

  override final def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    lookForResolver(resolvers, request)

  @scala.annotation.tailrec
  private def lookForResolver(res: List[ResolveIfApplies], request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    res match {
      case head :: tail =>
        head.resolve(request) match {
          case Some(rs) => rs
          case _        => lookForResolver(tail, request)
        }
      case Nil =>
        emptySuggestion
    }

  object ResolveDefault extends ResolveIfApplies {
    override def resolve(params: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
      applies(defaultStructure(params))
  }

  protected final def defaultStructure(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    if (isWritingProperty(params.yPartBranch))
      if (!isInFieldValue(params)) {
        val isEncoded = isEncodes(params.amfObject, params.actualDialect) && params.fieldEntry.isEmpty
        if (((isEncoded && params.yPartBranch.isAtRoot) || !isEncoded) && params.fieldEntry.isEmpty)
          new AMLStructureCompletionsPlugin(params.propertyMapping)
            .resolve(params.amfObject.meta.`type`.head.iri())
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

        new AMLStructureCompletionsPlugin(props).resolve(meta.`type`.head.iri())
      case _ => Nil
    }
  }
}
