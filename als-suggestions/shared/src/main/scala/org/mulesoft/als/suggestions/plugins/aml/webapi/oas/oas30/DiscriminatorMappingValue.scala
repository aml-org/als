package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.annotations.SourceNode
import amf.core.model.domain.{Linkable, Shape}
import amf.core.parser._
import amf.plugins.domain.shapes.metamodel.NodeShapeModel
import amf.plugins.domain.shapes.models.NodeShape
import amf.plugins.domain.webapi.metamodel.IriTemplateMappingModel
import amf.plugins.domain.webapi.models.IriTemplateMapping
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.ExceptionPlugin
import org.yaml.model.YMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DiscriminatorMappingValue extends ExceptionPlugin {
  override def id: String = "DiscriminatorMappingValue"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    request.amfObject match {
      case obj: NodeShape
          if request.fieldEntry
            .exists(_.field == NodeShapeModel.DiscriminatorMapping) && request.yPartBranch.isValue =>
        suggest(obj)
      case _: IriTemplateMapping if request.fieldEntry.exists(_.field == IriTemplateMappingModel.LinkExpression) =>
        request.branchStack.collectFirst({ case n: NodeShape => suggest(n) }).getOrElse(Nil)
      case _ => Nil
    }
  }

  private def suggest(n: NodeShape): Seq[RawSuggestion] =
    parents(n).collect({ case l: Linkable if l.isLink => extractRef(l) }).map(RawSuggestion(_, isAKey = false))

  private def extractRef(l: Linkable) = {
    l.annotations.find(classOf[SourceNode]).flatMap(_.node.toOption[YMap]).flatMap(_.entries.headOption) match {
      case Some(e) => e.value.asScalar.map(_.text).getOrElse(l.linkLabel.value())
      case _       => l.linkLabel.value()
    }
  }

  private def parents(n: NodeShape): Seq[Shape] = n.xone ++ n.or ++ n.and

  // ?????
  override def applies(request: AmlCompletionRequest): Boolean = {
    request.amfObject match {
      case _: NodeShape =>
        request.fieldEntry.exists(_.field == NodeShapeModel.DiscriminatorMapping) && request.yPartBranch.isValue
      case _: IriTemplateMapping => true
      case _                     => false
    }
  }
}
