package org.mulesoft.als.suggestions.plugins.aml.webapi

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.internal.metamodel.domain.PayloadModel
import amf.core.client.common.position.{Range => AmfRange}
import amf.core.client.common.validation.{ProfileName, ProfileNames}
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.client.scala.model.domain._
import amf.core.internal.parser.domain.Value
import amf.shapes.client.scala.model.domain.{AnyShape, ArrayShape, Example, NodeShape}
import amf.shapes.internal.domain.metamodel.ExampleModel
import amf.shapes.internal.domain.metamodel.common.ExamplesField
import amf.shapes.internal.domain.resolution.elements.CompleteShapeTransformationPipeline
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectExamplePropertiesCompletionPlugin(
    node: DataNode,
    dialect: Dialect,
    override val anyShape: AnyShape,
    example: Example,
    override protected val alsConfigurationState: ALSConfigurationState
) extends ShapePropertiesSuggestions {

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
      case _: ArrayNode if a.isInstanceOf[ArrayShape] =>
        Some(a.asInstanceOf[ArrayShape].items.asInstanceOf[NodeShape])
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
  protected val alsConfigurationState: ALSConfigurationState

  def suggest(): Seq[RawSuggestion] = shapeForObj.map(_.properties.map(propToRaw)).getOrElse(Nil)

  private def profile: ProfileName =
    if (dialect.id == OAS20Dialect.dialect.id) ProfileNames.OAS20 else ProfileNames.RAML10

  // todo: use specific amf configuration for resolution?
  protected val resolved: Option[AnyShape] =
    new CompleteShapeTransformationPipeline(anyShape, LocalIgnoreErrorHandler, profile)
      .transform(alsConfigurationState.configForDialect(dialect).config) match {
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

case class PureShapePropertiesSuggestions(
    override val anyShape: AnyShape,
    dialect: Dialect,
    override protected val alsConfigurationState: ALSConfigurationState
) extends ShapePropertiesSuggestions {
  override protected def shapeForObj: Option[NodeShape] = resolved.collectFirst({ case n: NodeShape => n })
}

trait ExampleSuggestionPluginBuilder {
  protected def buildPluginFromExample(
      e: Example,
      request: AmlCompletionRequest
  ): Option[ShapePropertiesSuggestions] = {
    findShape(e, request.branchStack).flatMap { case (e: Example, s: Shape) =>
      suggestionsForShape(e, s, request)
    }
  }

  protected def buildPluginFromField(request: AmlCompletionRequest): Option[ShapePropertiesSuggestions] = {
    request.fieldEntry
      .filter(fe => fe.field == ExamplesField.Examples)
      .flatMap(_ => {
        isFatherShape(Some(request.amfObject))
          .map(s => PureShapePropertiesSuggestions(s, request.actualDialect, request.alsConfigurationState))
      })
  }

  protected def buildPluginFromExampleObj(request: AmlCompletionRequest): Option[ShapePropertiesSuggestions] = {
    request.amfObject match {
      case e: Example
          if (request.yPartBranch.isKey || request.yPartBranch.isArray) && !e.fields.exists(
            ExampleModel.StructuredValue
          ) =>
        findShape(e, e +: request.branchStack).map(s =>
          PureShapePropertiesSuggestions(s._2, request.actualDialect, request.alsConfigurationState)
        )
      case _ => None
    }
  }

  protected def suggestionsForShape(
      e: Example,
      anyShape: AnyShape,
      request: AmlCompletionRequest
  ): Option[ObjectExamplePropertiesCompletionPlugin] = {
    findNode(request)
      .map(obj =>
        new ObjectExamplePropertiesCompletionPlugin(
          obj,
          request.actualDialect,
          anyShape,
          e,
          request.alsConfigurationState
        )
      )
  }

  private def isScalarNodeValue(parent: AmfObject, yPart: YPartBranch, s: ScalarNode) = {
    parent match {
      case o: ObjectNode if o.allProperties().toList.contains(s) =>
        s.position().exists(li => li.range == AmfRange(yPart.node.range))
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
            .flatMap(buildPluginFromExample(_, request))
        )
        .orElse(buildPluginFromField(request))
        .map(_.suggest())
        .getOrElse(Nil)
    }
  }
}
