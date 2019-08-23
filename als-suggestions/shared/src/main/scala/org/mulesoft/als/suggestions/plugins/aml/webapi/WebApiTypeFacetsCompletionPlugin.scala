package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.model.DataType
import amf.core.model.domain.extensions.PropertyShape
import amf.core.model.domain.{AmfObject, Shape}
import amf.core.parser.Value
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.document.webapi.annotations.Inferred
import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import amf.plugins.domain.shapes.models.{AnyShape, ScalarShape}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.Raml10TypesDialect
import org.mulesoft.als.suggestions.plugins.aml._
import scala.concurrent.Future

trait WebApiTypeFacetsCompletionPlugin extends AMLCompletionPlugin {
  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case shape: Shape if isWrittingFacet(params.yPartBranch, shape) =>
        resolveShape(shape, params.branchStack, params.indentation)
      case _ => Nil
    })
  }

  private def isWrittingFacet(yPartBranch: YPartBranch, shape: Shape): Boolean =
    yPartBranch.isKey && shape.name.value() != yPartBranch.stringValue

  def resolveShape(shape: Shape, branchStack: Seq[AmfObject], indentation: String): Seq[RawSuggestion] = {

    val node = shape match {
      case scalar: ScalarShape =>
        scalar.dataType.option() match {
          case Some(DataType.Decimal | DataType.Double | DataType.Float | DataType.Long | DataType.Number) =>
            Some(numberShapeNode)
          case Some(DataType.Integer) => Some(integerShapeNode)
          case _                      => Some(stringShapeNode)
        }
      case _ =>
        declarations
          .find(_.nodetypeMapping.option().contains(shape.meta.`type`.head.iri()))
    }

    val classSuggestions = node.map(n => n.propertiesRaw(indentation)).getOrElse(Nil)

    // corner case, property shape should suggest facets of the range PLUS required
    val finalSuggestions: Iterable[RawSuggestion] = (branchStack.headOption match {
      case Some(_: PropertyShape) =>
        (propertyShapeNode.map(_.propertiesRaw(indentation)).getOrElse(Nil) ++ classSuggestions).toSet
      case _ => classSuggestions
    }) ++ defaults(shape, indentation)

    finalSuggestions.toSeq
  }

  private def defaults(s: Shape, indentation: String): Seq[RawSuggestion] = {
    s match {
      case s: ScalarShape =>
        s.fields.getValueAsOption(ScalarShapeModel.DataType) match {
          case Some(Value(_, ann)) if ann.contains(classOf[Inferred]) && s.isInstanceOf[ScalarShape] =>
            Seq(RawSuggestion("properties", indentation, isAKey = true, "schemas"),
                RawSuggestion("items", indentation, isAKey = true, "schemas"))
          case _ => Nil
        }
      case a: AnyShape if a.isDefaultEmpty =>
        Seq(RawSuggestion("properties", indentation, isAKey = true, "schemas"),
            RawSuggestion("items", indentation, isAKey = true, "schemas"))
      case _ => Nil
    }
  }

  def stringShapeNode: NodeMapping
  def numberShapeNode: NodeMapping
  def integerShapeNode: NodeMapping

  def declarations: Seq[NodeMapping]
  def propertyShapeNode: Option[NodeMapping]
}
