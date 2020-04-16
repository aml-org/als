package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.ProfileNames
import amf.core.model.domain._
import amf.core.model.domain.extensions.PropertyShape
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.shapes.models.{AnyShape, ArrayShape, Example, NodeShape}
import amf.plugins.domain.shapes.resolution.stages.elements.CompleteShapeTransformationPipeline
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.LocalIgnoreErrorHandler
import org.mulesoft.amfmanager.dialect.webapi.oas.Oas20DialectWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectExamplePropertiesCompletionPlugin(objectNode: ObjectNode, dialect: Dialect, branch: Seq[AmfObject]) {

  private val profile = if (dialect.id == Oas20DialectWrapper.dialect.id) ProfileNames.OAS20 else ProfileNames.RAML

  def suggest(): Seq[RawSuggestion] = {
    val definitionNode =
      findAndResolveFirst().flatMap(shapeForObj(_, (e: Example) => branch.contains(e)))
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

  private def findAndResolveFirst(): Option[AnyShape] =
    branch.collectFirst({ case a: AnyShape => a }).map(resolve).collectFirst({ case a: AnyShape => a })

  private def resolve(a: AnyShape): Shape =
    new CompleteShapeTransformationPipeline(a, LocalIgnoreErrorHandler, profile).resolve()

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
          new ObjectExamplePropertiesCompletionPlugin(o, request.actualDialect, request.branchStack).suggest()
        case s: ScalarNode
            if request.yPartBranch.isJson && request.yPartBranch.stringValue == "x" && request.branchStack.headOption
              .exists(_.isInstanceOf[ArrayNode]) =>
          val o = ObjectNode(s.annotations)
          request.branchStack.headOption.collect({ case a: ArrayNode => a }).foreach { a =>
            a.withMembers(Seq(o))
          }
          new ObjectExamplePropertiesCompletionPlugin(o, request.actualDialect, request.branchStack).suggest()
        case _ => Nil
      }
    }
  }
}
