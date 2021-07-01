package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.{AmfObject, DomainElement}
import amf.plugins.document.vocabularies.annotations.DiscriminatorField
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{
  DialectDomainElement,
  NodeMappable,
  NodeMapping,
  PropertyMapping,
  UnionNodeMapping
}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AMLUnionCompletionPlugin extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    new AMLUnionCompletionPlugin(request).resolve()
}

class AMLUnionCompletionPlugin(params: AmlCompletionRequest) extends UnionSuggestions {
  override protected val amfObject: AmfObject     = params.amfObject
  override protected val dialect: Dialect         = params.actualDialect
  override protected val yPartBranch: YPartBranch = params.yPartBranch

  lazy val unionType: Option[UnionNodeMapping] = getUnionType

  // this plugin applies when we are on a union with no type discriminator or the type discriminator is not set
  def resolve(): Option[Future[Seq[RawSuggestion]]] =
    if (params.yPartBranch.isKey && !hasDefinedDiscriminator(amfObject)) {
      unionType.map(getSuggestions)
    } else None

  def hasDefinedDiscriminator(amfObject: AmfObject): Boolean = amfObject match {
    // We have a discriminator name and it's set
    case d: DialectDomainElement =>
      unionType.exists(_.typeDiscriminatorName().nonEmpty) && amfObject.annotations
        .find(classOf[DiscriminatorField])
        .exists(_.value != "")
    case _ => false
  }

  private def getSuggestions(unionMapping: UnionNodeMapping): Future[Seq[RawSuggestion]] = Future {
    val inherited: Seq[RawSuggestion] = getUnionProperties(unionMapping).map(_.toRaw("unknown"))
    inherited ++
      Option(unionMapping.typeDiscriminatorName().value())
        .map(name => RawSuggestion(name, isAKey = true, "", mandatory = true))
  }

}
