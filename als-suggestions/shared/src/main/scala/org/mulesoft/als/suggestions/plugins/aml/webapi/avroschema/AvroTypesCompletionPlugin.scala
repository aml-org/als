package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.client.scala.model.domain.{AmfArray, AmfObject, AmfScalar}
import amf.core.client.scala.traversal.iterator.AmfElementIterator
import amf.core.internal.metamodel.document.ModuleModel
import amf.core.internal.metamodel.domain.ShapeModel
import amf.shapes.client.scala.model.domain.{AnyShape, NodeShape}
import amf.shapes.client.scala.model.domain.jsonldinstance.JsonLDElement
import amf.shapes.internal.domain.metamodel.AnyShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLEnumCompletionPlugin.getSuggestions
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AvroTypesCompletionPlugin extends AMLCompletionPlugin with FieldTypeKnowledge {

  override def id: String = "AvroTypesCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (isInType(request)) Future {
      collectAllTypes(request.baseUnit)
        .map(t => RawSuggestion(t, isAKey = false))
        .toSeq
    }
    else emptySuggestion

  private def collectAllTypes(baseUnit: BaseUnit): Set[String] = {
    val iterator                            = AmfElementIterator(List(baseUnit))
    val collectedNames: mutable.Set[String] = mutable.Set()
    while (iterator.hasNext) {
      iterator.next() match {
        case shape: AnyShape =>
          shape.fields
            .fields()
            .find(t => t.field == AnyShapeModel.Name)
            .foreach { fe =>
              collectedNames.add(fe.value.value.toString)
            }
          shape.fields
            .fields()
            .find(t => t.field == AnyShapeModel.Aliases)
            .foreach { fe =>
              fe.arrayValues[AmfScalar].foreach {
                case AmfScalar(value, _) => collectedNames.add(value.toString)
                case _                   => // do nothing
              }
            }
        case _ => // do nothing
      }
    }
    collectedNames.toSet.filterNot(AvroDialect.avroTypes.contains)
  }

  private def isInType(request: AmlCompletionRequest): Boolean =
    (request.astPartBranch.isValue || request.astPartBranch.isInArray && request.fieldEntry.isDefined) &&
      isPropertyMappingFacet(request) || isFieldType(request)

  private def isPropertyMappingFacet(request: AmlCompletionRequest): Boolean =
    request.propertyMapping
      .find(_.id == AvroDialect.inheritsId)
      .exists(pm => request.astPartBranch.parentKey.exists(parent => pm.name().option().contains(parent)))

}
