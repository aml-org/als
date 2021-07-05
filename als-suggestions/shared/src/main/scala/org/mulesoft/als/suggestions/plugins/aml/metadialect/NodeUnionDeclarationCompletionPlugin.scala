package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.core.benchmark.ExecutionLog.executionContext
import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping, UnionNodeMapping}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.{AMLCompletionPlugin, ResolveIfApplies}
import org.mulesoft.als.suggestions.plugins.aml.{AMLUnionCompletionPlugin, PropertyMappingWrapper, UnionSuggestions}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.{NodeMappingObjectNode, UnionMappingObjectNode}

import scala.concurrent.Future

object NodeUnionDeclarationCompletionPlugin extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    new NodeUnionDeclarationCompletionPlugin(request).resolve()
}

class NodeUnionDeclarationCompletionPlugin(params: AmlCompletionRequest) extends UnionSuggestions {
  override protected val amfObject: AmfObject     = params.amfObject
  override protected val dialect: Dialect         = params.actualDialect
  override protected val yPartBranch: YPartBranch = params.yPartBranch

  def applies(): Boolean = {
    params.yPartBranch.isKey && (params.amfObject match {
      case nm: NodeMapping => true
      case _               => false
    })
  }

  def resolve(): Option[Future[Seq[RawSuggestion]]] =
    if (applies()) {
      Some(getSuggestions)
    } else None

  def getSuggestions: Future[Seq[RawSuggestion]] = Future {
    Seq(Some(UnionMappingObjectNode.Obj), Some(NodeMappingObjectNode.Obj))
      .flatMap(getProperties(_, None))
      .foldLeft(Seq[PropertyMapping]())(filterProperties)
      .map(_.toRaw("unknown"))
  }
}
