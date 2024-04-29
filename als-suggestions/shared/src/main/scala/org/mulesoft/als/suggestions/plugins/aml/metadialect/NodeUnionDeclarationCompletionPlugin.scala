package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.{PropertyMappingWrapper, UnionSuggestions}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{NodeMappingObjectNode, UnionMappingObjectNode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NodeUnionDeclarationCompletionPlugin extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.astPartBranch match {
      case yPart: YPartBranch => new NodeUnionDeclarationCompletionPlugin(request, yPart).resolve()
      case _                  => None
    }

  }
}

class NodeUnionDeclarationCompletionPlugin(
    params: AmlCompletionRequest,
    override protected val yPartBranch: YPartBranch
) extends UnionSuggestions {
  override protected val amfObject: AmfObject = params.amfObject
  override protected val dialect: Dialect     = params.actualDialect
  def applies(): Boolean =
    params.astPartBranch.isKey && params.amfObject.isInstanceOf[NodeMapping]

  def resolve(): Option[Future[Seq[RawSuggestion]]] =
    if (applies())
      Some(getSuggestions)
    else None

  def getSuggestions: Future[Seq[RawSuggestion]] = Future {
    Seq(Some(UnionMappingObjectNode.Obj), Some(NodeMappingObjectNode.Obj))
      .flatMap(getProperties(_, None))
      .foldLeft(Seq[PropertyMapping]())(filterProperties)
      .map(_.toRaw("unknown"))
  }
}
