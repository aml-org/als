package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.{DialectDomainElement, UnionNodeMapping}
import amf.aml.internal.annotations.FromUnionRangeMapping
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLUnionRangeCompletionPlugin extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    new AMLUnionRangeCompletionPlugin(request).resolve()
}

class AMLUnionRangeCompletionPlugin(params: AmlCompletionRequest) extends UnionSuggestions {
  override protected val amfObject: AmfObject     = params.amfObject
  override protected val dialect: Dialect         = params.actualDialect
  override protected val yPartBranch: YPartBranch = params.yPartBranch

  lazy val possibleMappings: Seq[String] = fromUnionRangeMapping(amfObject)

  // this plugin applies when we are on a union range
  // and xone shape regoModule or
  def resolve(): Option[Future[Seq[RawSuggestion]]] =
    if (params.yPartBranch.isKey && possibleMappings.nonEmpty) {
      Some(getSuggestions(possibleMappings))
    } else None

  def fromUnionRangeMapping(amfObject: AmfObject): Seq[String] =
    amfObject.annotations
      .find(classOf[FromUnionRangeMapping])
      .map { ann: FromUnionRangeMapping =>
        ann.possibleRanges
      }
      .getOrElse(Seq.empty)

  private def getSuggestions(unionMapping: Seq[String]): Future[Seq[RawSuggestion]] = Future {
    getUnionProperties(UnionNodeMapping().withObjectRange(unionMapping)).map(_.toRaw("unknown"))
  }

}
