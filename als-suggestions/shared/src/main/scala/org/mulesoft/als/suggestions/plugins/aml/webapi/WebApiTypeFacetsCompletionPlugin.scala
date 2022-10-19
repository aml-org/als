package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.NodeMapping
import amf.core.client.scala.model.DataType
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.client.scala.model.domain.{AmfObject, Shape}
import amf.core.internal.parser.domain.Value
import amf.shapes.client.scala.model.domain.{AnyShape, NodeShape, ScalarShape, UnresolvedShape}
import amf.shapes.internal.annotations.TypePropertyLexicalInfo
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp

import scala.concurrent.Future

trait WebApiTypeFacetsCompletionPlugin extends AMLCompletionPlugin with WritingShapeInfo {

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future.successful(params.amfObject match {
      case shape: Shape
          if isWritingFacet(
            params.astPartBranch,
            shape,
            params.branchStack,
            params.actualDialect
          ) && params.fieldEntry.isEmpty =>
        resolveShape(shape, params.branchStack, params.actualDialect)
      case _ => Nil
    })
  }

  def resolveShape(shape: Shape, branchStack: Seq[AmfObject], dialect: Dialect): Seq[RawSuggestion] = {
    val node = shape match {
      case scalar: ScalarShape =>
        scalar.dataType.option() match {
          case Some(DataType.Decimal | DataType.Double | DataType.Float | DataType.Long | DataType.Number) =>
            Some(numberShapeNode)
          case Some(DataType.Integer) => Some(integerShapeNode)
          case _ if scalar.annotations.isSynthesized => getSuggestionsForAnyShapeNode
          case _                      => Some(stringShapeNode)
        }
      case _: NodeShape if shape.fields.fields().exists(_.value.value.isInstanceOf[UnresolvedShape]) =>
        getSuggestionsForAnyShapeNode
      case _ =>
        // check inherits field to suggest specific.
        val s = findMoreSpecific(shape.metaURIs, declarations)
        s
    }

    val classSuggestions: Seq[RawSuggestion] =
      node.map(n => n.propertiesRaw(fromDialect = dialect)).getOrElse(Nil)

    // corner case, property shape should suggest facets of the range PLUS required
    val finalSuggestions: Iterable[RawSuggestion] = (branchStack.headOption match {
      case Some(_: PropertyShape) =>
        (propertyShapeNode
          .map(_.propertiesRaw(fromDialect = dialect))
          .getOrElse(Nil) ++ classSuggestions).toSet
      case _ => classSuggestions
    }) ++ defaults(shape)

    finalSuggestions.toSeq
  }

  private val getSuggestionsForAnyShapeNode: Option[NodeMapping] = {
    Some(anyShapeNode)
  }

  private def findMoreSpecific(iris: List[String], declarations: Seq[NodeMapping]): Option[NodeMapping] = {
    iris match {
      case Nil => None
      case head :: tail =>
        declarations.find(_.nodetypeMapping.option().contains(head)).orElse(findMoreSpecific(tail, declarations))
      case _ => None
    }
  }

  private def defaultSuggestions: Seq[RawSuggestion] =
    Seq(RawSuggestion.forObject("properties", "schemas"), RawSuggestion.forObject("items", "schemas"))

  protected def defaults(s: Shape): Seq[RawSuggestion] =
    s match {
      case s: ScalarShape =>
        s.fields.getValueAsOption(ScalarShapeModel.DataType) match {
          case Some(Value(_, ann))
              if (ann.isSynthesized || ann.isInferred) && s
                .isInstanceOf[ScalarShape] =>
            defaultSuggestions
          case _ => Nil
        }
      case a: AnyShape if a.annotations.find(classOf[TypePropertyLexicalInfo]).isEmpty => defaultSuggestions
      case _                                                                           => Nil
    }

  def stringShapeNode: NodeMapping
  def numberShapeNode: NodeMapping
  def integerShapeNode: NodeMapping

  def anyShapeNode: NodeMapping

  def declarations: Seq[NodeMapping]
  def propertyShapeNode: Option[NodeMapping]
}
