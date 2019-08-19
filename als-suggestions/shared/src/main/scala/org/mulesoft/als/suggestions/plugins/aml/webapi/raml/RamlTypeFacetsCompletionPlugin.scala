package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

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
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.Future

object RamlTypeFacetsCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "RamlTypeFacetsCompletionPlugin"

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
          case Some(
              DataType.Integer | DataType.Decimal | DataType.Double | DataType.Float | DataType.Long |
              DataType.Number) =>
            Some(Raml10TypesDialect.NumberShapeNode)
          case _ => Some(Raml10TypesDialect.StringShapeNode)
        }
      case _ =>
        Raml10TypesDialect.dialect.declares
          .collect({ case n: NodeMapping => n })
          .find(_.nodetypeMapping.option().contains(shape.meta.`type`.head.iri()))
    }

    val classSuggestions = node.map(n => n.propertiesRaw(indentation)).getOrElse(Nil)

    // corner case, property shape should suggest facets of the range PLUS required
    val finalSuggestions: Iterable[RawSuggestion] = (branchStack.headOption match {
      case Some(_: PropertyShape) =>
        (Raml10TypesDialect.PropertyShapeNode.propertiesRaw(indentation) ++ classSuggestions).toSet
      case _ => classSuggestions
    }) ++ defaults(shape, indentation)

    finalSuggestions.toSeq
  }

  private def defaults(s: Shape, indentation: String): Seq[RawSuggestion] = {
    s match {
      case s: ScalarShape =>
        s.fields.getValueAsOption(ScalarShapeModel.DataType) match {
          case Some(Value(_, ann)) if ann.contains(classOf[Inferred]) && s.isInstanceOf[ScalarShape] =>
            Seq(RawSuggestion("properties", indentation, isAKey = true),
                RawSuggestion("items", indentation, isAKey = true))
          case _ => Nil
        }
      case a: AnyShape if a.isDefaultEmpty =>
        Seq(RawSuggestion("properties", indentation, isAKey = true),
            RawSuggestion("items", indentation, isAKey = true))
      case _ => Nil
    }
  }
}
