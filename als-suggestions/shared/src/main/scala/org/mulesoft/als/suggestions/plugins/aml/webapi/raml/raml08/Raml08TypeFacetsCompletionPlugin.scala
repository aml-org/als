package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.aml.client.scala.model.domain.NodeMapping
import amf.apicontract.client.scala.model.domain.Payload
import amf.core.client.scala.model.domain.Shape
import amf.core.internal.annotations.Inferred
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.parser.domain.Value
import amf.shapes.client.scala.model.domain.{AnyShape, ScalarShape}
import amf.shapes.internal.annotations.TypePropertyLexicalInfo
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.PayloadMediaTypeSeeker
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect

import scala.concurrent.Future

object Raml08TypeFacetsCompletionPlugin extends WebApiTypeFacetsCompletionPlugin with PayloadMediaTypeSeeker {
  override def id: String = "RamlTypeFacetsCompletionPlugin"

  val formMediaTypes: Seq[String] =
    Seq("application/x-www-form-urlencoded", "multipart/form-data")

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case shape: Shape
          if isWritingFacet(params.astPartBranch, shape, params.branchStack, params.actualDialect) &&
            !isWritingKeyMediaType(params) &&
            !insideMediaType(params) =>
        resolveShape(shape, params.branchStack, params.actualDialect)
      case shape: Shape
          if isWritingFacet(params.astPartBranch, shape, params.branchStack, params.actualDialect) &&
            !isWritingKeyMediaType(params) =>
        {
          if (insideFormMediaType(params))
            Seq(RawSuggestion.forObject("formParameters", "schemas"))
          else Seq()
        } :+ RawSuggestion("schema", isAKey = true, "schemas", mandatory = false)
      case p: Payload if params.astPartBranch.isKey && p.mediaType.option().isDefined =>
        if (formMediaTypes.contains(p.mediaType.value()))
          Seq(
            RawSuggestion.forObject("formParameters", "schemas"),
            RawSuggestion("schema", isAKey = true, "schemas", mandatory = false)
          )
        else
          Seq(RawSuggestion("schema", isAKey = true, "schemas", mandatory = false))
      case _ => Nil
    })
  }

  override def defaults(s: Shape): Seq[RawSuggestion] =
    s match {
      case s: ScalarShape =>
        s.fields.getValueAsOption(ScalarShapeModel.DataType) match {
          case Some(Value(_, ann))
              if ann.contains(classOf[Inferred]) && s
                .isInstanceOf[ScalarShape] =>
            Seq(RawSuggestion.forBoolKey("repeat", "schemas"))
          case _ => Nil
        }
      case a: AnyShape if a.isNotExplicit =>
        Seq(RawSuggestion.forBoolKey("repeat", "schemas"))
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
              .contains(mt)
          )
      case _ => false
    }
}
