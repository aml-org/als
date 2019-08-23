package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.ProfileNames
import amf.core.model.domain._
import amf.core.model.domain.extensions.PropertyShape
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.shapes.models.{AnyShape, ArrayShape, Example, NodeShape}
import amf.plugins.domain.shapes.resolution.stages.elements.ShapeTransformationPipeline
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.Oas20DialectWrapper
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.LocalIgnoreErrorHandler
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class ObjectExamplePropertiesCompletionPlugin(objectNode: ObjectNode, dialect:Dialect,branch: Seq[AmfObject]) {

  private val profile = if (dialect.id == Oas20DialectWrapper.dialect.id ) ProfileNames.OAS20 else ProfileNames.RAML

  def suggest(indentation:String):Seq[RawSuggestion] = {
    val definitionNode =
      findAndResolveFirst().flatMap(shapeForObj(_, (e: Example) => branch.contains(e)))
    definitionNode.map(_.properties.map(propToRaw(_, indentation))).getOrElse(Nil)
  }

  private def propToRaw(propertyShape: PropertyShape, indentation: String) = {
    propertyShape.range match {
      case _: NodeShape | _: ArrayShape =>
        RawSuggestion(propertyShape.name.value(), indentation, isAKey = true, "example")
      case _ =>
        RawSuggestion.forKey(propertyShape.name.value(), "example")
    }
  }

  private def findAndResolveFirst(): Option[AnyShape] =
    branch.collectFirst({ case a: AnyShape => a }).map(resolve).collectFirst({ case a: AnyShape => a })

  private def resolve(a: AnyShape): Shape =
    new ShapeTransformationPipeline(a, LocalIgnoreErrorHandler, profile).resolve()

  private def shapeForObj(a: AnyShape, findFn: Example => Boolean) =
    a.examples.find(findFn).flatMap(e => findNode(a, e.structuredValue, objectNode))

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

object ObjectExamplePropertiesCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "ObjectExamplePropertiesCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case o: ObjectNode if request.branchStack.exists(_.isInstanceOf[Shape]) && request.yPartBranch.isKey =>
          new ObjectExamplePropertiesCompletionPlugin(o, request.actualDialect, request.branchStack)
            .suggest(request.indentation)
        case _ => Nil
      }
    }
  }
}
