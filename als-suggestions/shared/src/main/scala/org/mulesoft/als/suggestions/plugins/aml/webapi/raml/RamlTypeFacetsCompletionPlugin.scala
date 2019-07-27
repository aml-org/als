package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.metamodel.domain.ShapeModel
import amf.core.model.DataType
import amf.core.model.domain.extensions.PropertyShape
import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.domain.shapes.models.ScalarShape
import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.{CompletionParams, RawSuggestion}

import scala.concurrent.Future

object RamlTypeFacetsCompletionPlugin extends CompletionPlugin {
  override def id: String = "RamlTypeFacetsCompletionPlugin"

  override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case shape: Shape if params.yPartBranch.isKey && !params.fieldEntry.exists(f => f.field == ShapeModel.Name) =>
        resolveShape(shape, params.branchStack)
      case _ => Nil
    })
  }

  def resolveShape(shape: Shape, branchStack: Seq[AmfObject]): Seq[RawSuggestion] = {
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

    val classSuggestions = node.map(n => n.propertiesNames).getOrElse(Nil)

    // corner case, property shape should suggest facets of the range PLUS required
    val finalSuggestions: Iterable[String] =
      if (branchStack.head.isInstanceOf[PropertyShape])
        (Raml10TypesDialect.PropertyShapeNode.propertiesNames ++ classSuggestions).toSet
      else classSuggestions

    finalSuggestions.map(RawSuggestion.forKey).toSeq
  }

}
