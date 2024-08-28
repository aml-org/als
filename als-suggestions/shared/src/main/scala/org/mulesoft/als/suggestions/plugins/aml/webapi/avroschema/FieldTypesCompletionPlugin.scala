package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfScalar
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.client.scala.traversal.iterator.AmfElementIterator
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.internal.domain.metamodel.AnyShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FieldTypesCompletionPlugin extends AMLCompletionPlugin with FieldTypeKnowledge {

  override def id: String = "FieldTypesCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (isFieldType(request)) Future {
      AvroDialect.avroTypes
        .map(t => RawSuggestion(t, isAKey = false))
    }
    else emptySuggestion
}
