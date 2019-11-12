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
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AMLStructureCompletionsPlugin(propertyMapping: Seq[PropertyMapping]) {

  def resolve(classTerm: String): Seq[RawSuggestion] =
    propertyMapping.map(p => p.toRaw(CategoryRegistry(classTerm, p.name().value())))

}

object AMLStructureCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLStructureCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (isWritingProperty(params.yPartBranch)) {
        if (!isInFieldValue(params)) {
          val isEncoded = isEncodes(params.amfObject, params.actualDialect) && params.fieldEntry.isEmpty
          if (((isEncoded && params.yPartBranch.isAtRoot) || !isEncoded) && params.fieldEntry.isEmpty) {
            new AMLStructureCompletionsPlugin(params.propertyMapping)
              .resolve(params.amfObject.meta.`type`.head.iri())
          } else Nil
        } else resolveObjInArray(params)
      } else Nil
    }
  }

  private def isWritingProperty(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKey || (yPartBranch.isJson && (yPartBranch.isInArray && yPartBranch.stringValue == "x"))

  protected def objInArray(params: AmlCompletionRequest): Option[DomainElementModel] = {
    params.fieldEntry match {
      case Some(FieldEntry(Field(t: ArrayLike, _, _, _), value))
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
