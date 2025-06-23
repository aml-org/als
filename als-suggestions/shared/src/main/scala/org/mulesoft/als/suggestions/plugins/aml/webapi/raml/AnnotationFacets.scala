package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.client.scala.model.domain.{AmfScalar, Shape}
import amf.core.client.scala.model.domain.extensions.CustomDomainProperty
import amf.core.internal.annotations.Inferred
import amf.core.internal.parser.domain.Annotations
import amf.shapes.client.scala.model.domain.ScalarShape
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08.Raml08TypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10TypeFacetsCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AnnotationFacets extends AMLCompletionPlugin {
  override def id: String = "AnnotationFacets"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case _: CustomDomainProperty if request.astPartBranch.isKey && request.fieldEntry.isEmpty => typeFacets(request)
        case s: Shape
            if request.branchStack.headOption
              .exists(_.isInstanceOf[CustomDomainProperty]) && isWrittingFacet(request) =>
          Raml10TypesDialect.AnnotationType.propertiesRaw(fromDefinition = request.actualDocumentDefinition)
        case _ => Nil
      }
    }
  }

  private def isWrittingFacet(request: AmlCompletionRequest): Boolean =
    request.astPartBranch.isKey && (request.amfObject match {
      case s: Shape => s.name.value() != request.astPartBranch.stringValue
      case _        => false
    })

  private def typeFacets(request: AmlCompletionRequest) = {
    val plugin =
      if (request.actualDocumentDefinition.baseUnit.id == Raml08TypesDialect.DialectLocation) Raml08TypeFacetsCompletionPlugin
      else Raml10TypeFacetsCompletionPlugin
    plugin.resolveShape(
      ScalarShape().set(ScalarShapeModel.DataType, AmfScalar("string"), Annotations() += Inferred()),
      Nil,
      request.actualDocumentDefinition
    )
  }
}
