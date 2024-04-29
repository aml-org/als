package org.mulesoft.als.suggestions.plugins.aml

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain._
import amf.aml.internal.annotations.FromUnionNodeMapping
import amf.core.client.scala.model.StrField
import amf.core.client.scala.model.domain.{AmfObject, DomainElement}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp

trait UnionSuggestions {
  protected val amfObject: AmfObject
  protected val dialect: Dialect
  protected val yPartBranch: YPartBranch

  private def getUnionTypeFromMeta: Option[UnionNodeMapping] =
    amfObject.metaURIs.flatMap { v =>
      dialect.declares.collectFirst({
        case d: UnionNodeMapping if d.id == v => d
      })
    }.headOption

  protected lazy val unionType: Option[UnionNodeMapping] =
    getUnionTypeFromAnnotation orElse getUnionTypeFromMeta

  private def getUnionTypeFromAnnotation: Option[UnionNodeMapping] =
    amfObject.annotations.find(classOf[FromUnionNodeMapping]).flatMap { ann: FromUnionNodeMapping =>
      dialect.declares.collectFirst({
        case d: UnionNodeMapping if d.id == ann.id => d
      })
    }

  protected def getDeclaredDomainElement(id: String): Option[DomainElement] =
    dialect.declares.collectFirst({
      case d if d.id == id => d
    })

  lazy val definedProperties: Set[String] = yPartBranch.brothersKeys

  protected def hasAllDefinedProperties(value: NodeMapping, typeDiscriminatorName: Option[String]): Boolean =
    definedProperties
      .filterNot(typeDiscriminatorName.contains)
      .forall(property => value.propertiesMapping().exists(_.name().value() == property))

  protected def getUnionProperties(unionMapping: UnionNodeMapping): Seq[PropertyMapping] =
    unionMapping
      .objectRange()
      .map(_.value())
      .map(getDeclaredDomainElement)
      .flatMap(getProperties(_, unionMapping.typeDiscriminatorName().option()))
      // filter duplicates (same property defined in multiple ranges of the union)
      .foldLeft(Seq[PropertyMapping]())(filterProperties)

  protected def getProperties(
      nm: Option[DomainElement],
      typeDiscriminatorName: Option[String] = None
  ): Seq[PropertyMapping] =
    nm match {
      case Some(value: UnionNodeMapping) => getUnionProperties(value)
      case Some(value: NodeMapping) if hasAllDefinedProperties(value, typeDiscriminatorName) =>
        value.propertiesMapping()
      case _ => Seq.empty
    }

  protected def filterProperties(acc: Seq[PropertyMapping], prop: PropertyMapping): Seq[PropertyMapping] =
    if (acc.exists(p => isDuplicateOf(p, prop))) acc else acc :+ prop

  private def isDuplicateOf(p: PropertyMapping, that: PropertyMapping): Boolean =
    p.name().value() == that.name().value() &&
      comparesObjectRange(p, that) &&
      p.allowMultiple().value() == that.allowMultiple().value()

  private def comparesObjectRange(p: PropertyMapping, t: PropertyMapping): Boolean = t.classification() match {
    case ObjectPairProperty =>
      comparesObjectProperty(p.objectRange().headOption, t.objectRange().headOption)
    case _ => p.objectRange().headOption == t.objectRange().headOption
  }

  private def comparesObjectProperty(p: Option[StrField], t: Option[StrField]): Boolean = (p, t) match {
    case (Some(p), Some(t)) =>
      p.annotations() == t.annotations() &&
      p.value() == t.value()
    case _ => false
  }
}
