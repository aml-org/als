package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.core.model.domain._
import amf.core.model.domain.extensions.PropertyShape
import amf.core.parser.Value
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.shapes.metamodel.common.ExamplesField
import amf.plugins.domain.shapes.models.{AnyShape, ArrayShape, Example, NodeShape}
import amf.plugins.domain.shapes.resolution.stages.elements.CompleteShapeTransformationPipeline
import amf.plugins.domain.webapi.metamodel.PayloadModel
import amf.{ProfileName, ProfileNames}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect
import amf.core.parser.Range

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectExamplePropertiesCompletionPlugin(node: DataNode,
                                                   dialect: Dialect,
                                                   override val anyShape: AnyShape,
                                                   example: Example)
    extends ShapePropertiesSuggestions {

  override protected def shapeForObj: Option[NodeShape] = resolved.flatMap(findNode(_, example.structuredValue, node))

  private def findNode(a: Shape, actual: DataNode, node: DataNode): Option[NodeShape] = {
    actual match {
      case o: ObjectNode if a.isInstanceOf[NodeShape] =>
        val n = a.asInstanceOf[NodeShape]
        if (o == node) Some(n)
        else {
          val propsE = o.allPropertiesWithName()
          n.properties.flatMap(p => propsE.get(p.name.value()).flatMap(v => findNode(p.range, v, node))).headOption
        }
      case arr: ArrayNode if a.isInstanceOf[ArrayShape] =>
        val items = a.asInstanceOf[ArrayShape].items
        arr.members.flatMap(mem => findNode(items, mem, node)).headOption
      case arr: ArrayNode if a.isInstanceOf[NodeShape] =>
        val n = a.asInstanceOf[NodeShape]
        if (arr == node) Some(n)
        else None
      case _ if a.isInstanceOf[NodeShape] => Some(a.asInstanceOf[NodeShape])
      case _                              => None

    }
  }
}

trait ShapePropertiesSuggestions {
  val anyShape: AnyShape
  protected val dialect: Dialect
  protected def shapeForObj: Option[NodeShape]

  def suggest(): Seq[RawSuggestion] = shapeForObj.map(_.properties.map(propToRaw)).getOrElse(Nil)

  private def profile: ProfileName =
    if (dialect.id == OAS20Dialect.dialect.id) ProfileNames.OAS20 else ProfileNames.RAML

  protected val resolved: Option[AnyShape] =
    new CompleteShapeTransformationPipeline(anyShape, LocalIgnoreErrorHandler, profile).resolve() match {
      case a: AnyShape => Some(a)
      case _           => None
    }

  private def propToRaw(propertyShape: PropertyShape) = {
    propertyShape.range match {
      case _: NodeShape | _: ArrayShape =>
        RawSuggestion(propertyShape.name.value(), isAKey = true, "example", propertyShape.minCount.value() > 0)
      case _ =>
        RawSuggestion.forKey(propertyShape.name.value(), "example", propertyShape.minCount.value() > 0)
    }
  }
}

case class PureShapePropertiesSuggestions(override val anyShape: AnyShape, dialect: Dialect)
    extends ShapePropertiesSuggestions {
  override protected def shapeForObj: Option[NodeShape] = resolved.collectFirst({ case n: NodeShape => n })
}

trait ExampleSuggestionPluginBuilder {
  protected def buildPluginFromExample(e: Example, request: AmlCompletionRequest): Option[ShapePropertiesSuggestions] = {
    findShape(e, request.branchStack).flatMap {
      case (e: Example, s: Shape) => suggestionsForShape(e, s, request)
    }
  }

  protected def buildPluginFromField(request: AmlCompletionRequest): Option[ShapePropertiesSuggestions] = {
    request.fieldEntry
      .filter(fe => fe.field == ExamplesField.Examples)
      .flatMap(_ => {
        isFatherShape(Some(request.amfObject)).map(s => PureShapePropertiesSuggestions(s, request.actualDialect))
      })
  }

  protected def buildPluginFromExampleObj(request: AmlCompletionRequest): Option[ShapePropertiesSuggestions] = {
    request.amfObject match {
      case e: Example
          if (request.yPartBranch.isKey || request.yPartBranch.isArray) && !e.fields.exists(
            ExampleModel.StructuredValue) =>
        findShape(e, e +: request.branchStack).map(s => PureShapePropertiesSuggestions(s._2, request.actualDialect))
      case _ => None
    }
  }

  protected def suggestionsForShape(e: Example,
                                    anyShape: AnyShape,
                                    request: AmlCompletionRequest): Option[ObjectExamplePropertiesCompletionPlugin] = {
    findNode(request)
      .map(obj => new ObjectExamplePropertiesCompletionPlugin(obj, request.actualDialect, anyShape, e))
  }

  private def isScalarNodeValue(parent: AmfObject, yPart: YPartBranch, s: ScalarNode) = {
    parent match {
      case o: ObjectNode if o.allProperties().toList.contains(s) =>
        s.position().exists(li => li.range == Range(yPart.node.range))
      case _ => false
    }
  }

  private def findNode(request: AmlCompletionRequest): Option[DataNode] = {
    request.amfObject match {
      case o: ObjectNode if request.yPartBranch.isKey => Some(o)
      case a: ArrayNode                               => Some(a)
      case s: ScalarNode if request.branchStack.headOption.exists(p => isScalarNodeValue(p, request.yPartBranch, s)) =>
        Some(s)
      case _: ScalarNode
          if request.branchStack.headOption.exists(_.isInstanceOf[ObjectNode]) && request.yPartBranch.isKey =>
        request.branchStack.headOption.collectFirst({ case o: ObjectNode => o })
      case s: ScalarNode =>
        val o = ObjectNode(s.annotations)
        request.branchStack.headOption.collect({ case a: ArrayNode => a }).foreach { a =>
          a.withMembers(Seq(o))
        }
        Some(o)
      case _ => None
    }
  }

  protected def findShape(e: Example, branch: Seq[AmfObject]): Option[(Example, AnyShape)] = {
    val i = branch.indexOf(e) + 1
    isFatherShape(branch.splitAt(Math.min(i, branch.length))._2.headOption).map(a => (e, a))
  }

  private def isFatherShape(s: Option[AmfObject]) = {
    s match {
      case Some(s: AnyShape) => Some(s)
      case Some(other) =>
        other.fields
          .getValueAsOption(PayloadModel.Schema)
          .collect({ case Value(s: AnyShape, _) => s }) // same uri than ParameterModel.schema
      case _ => None
    }
  }
}

object ObjectExamplePropertiesCompletionPlugin extends AMLCompletionPlugin with ExampleSuggestionPluginBuilder {
  override def id: String = "ObjectExamplePropertiesCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      buildPluginFromExampleObj(request)
        .orElse(
          request.branchStack
            .collectFirst({ case e: Example => e })
            .flatMap(buildPluginFromExample(_, request)))
        .orElse(buildPluginFromField(request))
        .map(_.suggest())
        .getOrElse(Nil)
    }
  }
}
