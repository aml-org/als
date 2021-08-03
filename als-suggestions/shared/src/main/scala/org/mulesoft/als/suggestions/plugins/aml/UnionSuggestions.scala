package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.domain.{AmfObject, DomainElement}
import amf.plugins.document.vocabularies.annotations.FromUnionNodeMapping
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{NodeMapping, PropertyMapping, UnionNodeMapping}
import org.mulesoft.als.common.YPartBranch

trait UnionSuggestions {
  protected val amfObject: AmfObject
  protected val dialect: Dialect
  protected val yPartBranch: YPartBranch

  protected def getUnionType: Option[UnionNodeMapping] =
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
      .toSeq

  protected def getProperties(nm: Option[DomainElement],
                              typeDiscriminatorName: Option[String] = None): Seq[PropertyMapping] =
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
      p.objectRange().headOption == that.objectRange().headOption &&
      p.allowMultiple().value() == that.allowMultiple().value()
}
