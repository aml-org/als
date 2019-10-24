package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.core.metamodel.domain.ShapeModel
import amf.core.model.domain.Shape
import amf.core.parser.Value
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.document.webapi.annotations.Inferred
import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import amf.plugins.domain.shapes.models.{AnyShape, ScalarShape}
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.PayloadMediaTypeSeeker
import org.mulesoft.amfmanager.dialect.webapi.raml.raml08.Raml08TypesDialect

import scala.concurrent.Future

object Raml08TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin with PayloadMediaTypeSeeker {
  override def id: String = "RamlTypeFacetsCompletionPlugin"

  private val formMediaTypes: Seq[String] =
    Seq("application/x-www-form-urlencoded", "multipart/form-data")

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case shape: Shape
          if isWritingFacet(params.yPartBranch, shape, params.branchStack) &&
            !isWritingKEYMediaType(params) &&
            !insideMediaType(params) =>
        resolveShape(shape, params.branchStack, params.indentation)
      case shape: Shape
          if isWritingFacet(params.yPartBranch, shape, params.branchStack) &&
            !isWritingKEYMediaType(params) => {
        if (insideFormMediaType(params))
          Seq(RawSuggestion("formParameters", params.indentation, isAKey = true, "schemas"))
        else Seq()
      } :+ RawSuggestion("schema", "", isAKey = true, "schemas")
      case p: Payload
          if formMediaTypes
            .contains(p.mediaType.value()) =>
        Seq(RawSuggestion("formParameters", params.indentation, isAKey = true, "schemas"),
            RawSuggestion("schema", "", isAKey = true, "schemas"))
      case _ => Nil
    })
  }

  override def defaults(s: Shape, indentation: String): Seq[RawSuggestion] =
    s match {
      case s: ScalarShape =>
        s.fields.getValueAsOption(ScalarShapeModel.DataType) match {
          case Some(Value(_, ann))
              if ann.contains(classOf[Inferred]) && s
                .isInstanceOf[ScalarShape] =>
            Seq(RawSuggestion("repeat", " ", isAKey = true, "schemas"))
          case _ => Nil
        }
      case a: AnyShape if a.isDefaultEmpty =>
        Seq(RawSuggestion("repeat", " ", isAKey = true, "schemas"))
      case _ => Nil
    }

  override def stringShapeNode: NodeMapping = Raml08TypesDialect.StringShapeNode

  override def numberShapeNode: NodeMapping = Raml08TypesDialect.NumberShapeNode

  override def integerShapeNode: NodeMapping =
    Raml08TypesDialect.NumberShapeNode

  override def declarations: Seq[NodeMapping] =
    Raml08TypesDialect.dialect.declares.collect({ case n: NodeMapping => n })

  override def propertyShapeNode: Option[NodeMapping] =
    Some(Raml08TypesDialect.PropertyShapeNode)

  private def insideFormMediaType(request: AmlCompletionRequest): Boolean =
    request.branchStack.headOption match {
      case Some(p: Payload) =>
        p.schema.fields
          .filter(f => f._1 != ShapeModel.Name)
          .fields()
          .isEmpty && p.mediaType
          .option()
          .exists(mt =>
            formMediaTypes
              .contains(mt))
      case _ => false
    }
}
