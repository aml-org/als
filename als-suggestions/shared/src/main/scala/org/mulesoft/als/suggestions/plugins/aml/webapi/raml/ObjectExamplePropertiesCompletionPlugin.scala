package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.ProfileNames
import amf.core.model.domain._
import amf.core.model.domain.extensions.PropertyShape
import amf.core.resolution.stages.ReferenceResolutionStage
import amf.plugins.document.webapi.resolution.stages.Branch
import amf.plugins.domain.shapes.metamodel.ArrayShapeModel
import amf.plugins.domain.shapes.models._
import amf.plugins.domain.shapes.resolution.stages.elements.ShapeTransformationPipeline
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ObjectExamplePropertiesCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "ObjectExamplePropertiesCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case o: ObjectNode if request.branchStack.exists(_.isInstanceOf[Shape]) && request.yPartBranch.isKey =>
          suggest(request.branchStack, o, request.indentation)
        case _ => Nil
      }
    }
  }

  private def suggest(branch: Seq[AmfObject], objectNode: ObjectNode, indentation: String) = {
    val definitionNode =
      findAndResolveFirst(branch).flatMap(shapeForObj(_, objectNode, (e: Example) => branch.contains(e)))
    definitionNode.map(_.properties.map(propToRaw(_, indentation))).getOrElse(Nil)
  }

  private def propToRaw(propertyShape: PropertyShape, indentation: String) = {
    propertyShape.range match {
      case _: NodeShape | _: ArrayShape =>
        RawSuggestion(propertyShape.name.value(), indentation, isAKey = true, "parameters")
      case _ =>
        RawSuggestion.forKey(propertyShape.name.value(), "parameters")
    }
  }

  private def findAndResolveFirst(branch: Seq[AmfObject]): Option[AnyShape] =
    branch.collectFirst({ case a: AnyShape => a }).map(resolve).collectFirst({ case a: AnyShape => a })

  private def resolve(a: AnyShape): Shape =
    new ShapeTransformationPipeline(a, LocalIgnoreErrorHandler, ProfileNames.RAML).resolve()

  private def shapeForObj(a: AnyShape, obj: ObjectNode, findFn: Example => Boolean) =
    a.examples.find(findFn).flatMap(e => findNode(a, e.structuredValue, obj))

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
      case _ => None
    }
  }

}
