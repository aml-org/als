package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.ProfileNames
import amf.core.model.domain._
import amf.core.model.domain.extensions.PropertyShape
import amf.core.parser.Value
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.shapes.models.{AnyShape, ArrayShape, Example, NodeShape}
import amf.plugins.domain.shapes.resolution.stages.elements.CompleteShapeTransformationPipeline
import amf.plugins.domain.webapi.metamodel.PayloadModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.LocalIgnoreErrorHandler
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectExamplePropertiesCompletionPlugin(objectNode: ObjectNode,
                                                   dialect: Dialect,
                                                   shape: AnyShape,
                                                   example: Example) {

  private val profile = if (dialect.id == OAS20Dialect.dialect.id) ProfileNames.OAS20 else ProfileNames.RAML

  def suggest(): Seq[RawSuggestion] = {
    val definitionNode = findAndResolveFirst().flatMap(shapeForObj)
    definitionNode.map(_.properties.map(propToRaw)).getOrElse(Nil)
  }

  private def propToRaw(propertyShape: PropertyShape) = {
    propertyShape.range match {
      case _: NodeShape | _: ArrayShape =>
        RawSuggestion(propertyShape.name.value(), isAKey = true, "example", propertyShape.minCount.value() > 0)
      case _ =>
        RawSuggestion.forKey(propertyShape.name.value(), "example", propertyShape.minCount.value() > 0)
    }
  }

  private def findAndResolveFirst(): Option[AnyShape] = resolve(shape) match {
    case a: AnyShape => Some(a)
    case _           => None
  }

  private def resolve(a: AnyShape): Shape =
    new CompleteShapeTransformationPipeline(a, LocalIgnoreErrorHandler, profile).resolve()

  private def shapeForObj(a: AnyShape) = findNode(a, example.structuredValue, objectNode)

  private def findNode(a: Shape, actual: DataNode, obj: ObjectNode): Option[NodeShape] = {
    actual match {
      case o: ObjectNode if a.isInstanceOf[NodeShape] =>
        val n = a.asInstanceOf[NodeShape]
        if (o == obj) Some(n)
        else {
          val propsE = o.allPropertiesWithName()
          n.properties.flatMap(p => propsE.get(p.name.value()).flatMap(v => findNode(p.range, v, obj))).headOption
        }
      case arr: ArrayNode if a.isInstanceOf[ArrayShape] =>
        val items = a.asInstanceOf[ArrayShape].items
        arr.members.flatMap(mem => findNode(items, mem, obj)).headOption
      case _ if a.isInstanceOf[NodeShape] => Some(a.asInstanceOf[NodeShape])
      case _                              => None

    }
  }
}

object ObjectExamplePropertiesCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "ObjectExamplePropertiesCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.branchStack
        .collectFirst({ case e: Example => e })
        .flatMap(findShape(_, request.branchStack))
        .map {
          case (e: Example, s: Shape) => suggestionsForShape(e, s, request)
        }
        .getOrElse(Nil)
    }
  }

  private def suggestionsForShape(e: Example, anyShape: AnyShape, request: AmlCompletionRequest) = {
    findNode(request)
      .map(obj => new ObjectExamplePropertiesCompletionPlugin(obj, request.actualDialect, anyShape, e).suggest())
      .getOrElse(Nil)
  }

  private def findNode(request: AmlCompletionRequest): Option[ObjectNode] = {
    request.amfObject match {
      case o: ObjectNode if request.yPartBranch.isKey => Some(o)
      case s: ScalarNode
          if request.yPartBranch.isJson && request.yPartBranch.stringValue == "x" && request.branchStack.headOption
            .exists(_.isInstanceOf[ArrayNode]) =>
        val o = ObjectNode(s.annotations)
        request.branchStack.headOption.collect({ case a: ArrayNode => a }).foreach { a =>
          a.withMembers(Seq(o))
        }
        Some(o)
      case _ => None
    }
  }
  private def findShape(e: Example, branch: Seq[AmfObject]): Option[(Example, AnyShape)] = {
    val i = branch.indexOf(e) + 1
    branch.splitAt(Math.min(i, branch.length))._2.headOption match {
      case Some(s: AnyShape) => Some(e, s)
      case Some(other) =>
        other.fields
          .getValueAsOption(PayloadModel.Schema)
          .collect({ case Value(s: AnyShape, _) => (e, s) }) // same uri than ParameterModel.schema
      case _ => None
    }
  }
}
