package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.annotations.DiscriminatorField
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{
  DialectDomainElement,
  NodeMapping,
  PropertyMapping,
  UnionNodeMapping
}
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
  override protected val amfObject: AmfObject  = params.amfObject
  override protected val dialect: Dialect      = params.actualDialect
  lazy val unionType: Option[UnionNodeMapping] = getUnionType

  lazy val definedProperties: Set[String] = params.yPartBranch.brothersKeys

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
    val inherited: Seq[RawSuggestion] = getPropertiesMapping(unionMapping).map(_.toRaw(""))
    inherited ++
      Option(unionMapping.typeDiscriminatorName().value())
        .map(name => RawSuggestion(name, isAKey = true, "", mandatory = true))
  }

  private def getPropertiesMapping(unionMapping: UnionNodeMapping): Seq[PropertyMapping] = {
    unionMapping
      .objectRange()
      .map(_.value())
      .map(getDeclaredDomainElement)
      .flatMap({
        case Some(value: UnionNodeMapping) => getPropertiesMapping(value)
        case Some(value: NodeMapping)
            if hasAllDefinedProperties(value, unionMapping.typeDiscriminatorName().option()) =>
          value.propertiesMapping()
        case _ => Seq.empty
      })
      // filter duplicates (same property defined in multiple ranges of the union)
      .foldLeft(Seq[PropertyMapping]())({
        case (acc, prop: PropertyMapping) if !acc.exists(p => isDuplicateOf(p, prop)) => acc :+ prop
        case (acc, _)                                                                 => acc
      })
  }

  private def hasAllDefinedProperties(value: NodeMapping, typeDiscriminatorName: Option[String]): Boolean =
    definedProperties
      .filterNot(typeDiscriminatorName.contains)
      .forall(property => value.propertiesMapping().exists(_.name().value() == property))

  private def isDuplicateOf(p: PropertyMapping, that: PropertyMapping): Boolean =
    p.name().value() == that.name().value() &&
      p.objectRange().headOption == that.objectRange().headOption &&
      p.allowMultiple().value() == that.allowMultiple().value()

}
