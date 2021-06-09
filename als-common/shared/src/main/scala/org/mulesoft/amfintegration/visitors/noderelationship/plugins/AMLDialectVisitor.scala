package org.mulesoft.amfintegration.visitors.noderelationship.plugins

import amf.core.model.StrField
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, AmfScalar}
import amf.core.parser.YNodeLikeOps
import amf.plugins.document.vocabularies.metamodel.domain.{PropertyMappingModel, UnionNodeMappingModel}
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.{DocumentsModel, NodeMapping, PropertyMapping, UnionNodeMapping}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.DialectElementVisitorFactory
import org.mulesoft.amfintegration.visitors.noderelationship.NodeRelationshipVisitorType
import org.yaml.model.{YMap, YMapEntry, YNode, YNodePlain, YSequence, YType}
class AMLDialectVisitor(d: Dialect) extends NodeRelationshipVisitorType {

  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element match {
      case dm: DocumentsModel =>
        (extractDeclarations(d, dm) :+ extractEncoded(d, dm)).flatten
      case nm: PropertyMapping => extractRanges(d, nm)
      case o: UnionNodeMapping => extractUnions(d, o)
      case o: NodeMapping      => extractExtends(d, o)
      case _                   => Seq.empty
    }
  private def extractRanges(d: Dialect, nm: PropertyMapping) =
    nm.annotations
      .ast()
      .collect {
        case m: YMap => m
        case ynp: YNodePlain =>
          ynp.value match {
            case m: YMap => m
          }
      }
      .map(_.entries
        .filter(e => e.key.asScalar.map(_.text).contains("range")))
      .map(
        s =>
          s.flatMap(
            e =>
              getLink(nm)
                .flatMap(getPositionForLink(d, _)
                  .map(t => RelationshipLink(e, t)))))
      .getOrElse(Seq.empty)

  private def extractUnions(d: Dialect, o: UnionNodeMapping) =
    o.fields.field[Seq[StrField]](UnionNodeMappingModel.ObjectRange) match {
      case a: Seq[StrField] =>
        val linkName = a.flatMap(_.option()).map(link => (link, link.split("/").last))
        extractUnionArray(d, o, linkName) ++
          extractDiscriminatorTypes(d, o, linkName)
      case _ => Seq.empty // nothing
    }

  private def extractUnionArray(d: Dialect, o: UnionNodeMapping, linkName: Seq[(String, String)]) =
    o.annotations.ast().flatMap {
      case a: YSequence => Some(a)
      case a: YMap =>
        a.entries.find(_.key.value.toString == "union").map(_.value.value)
      case _ => None
    } match {
      case Some(a: YSequence) =>
        linkName
          .flatMap(t => {
            a.children.find(_.toString == t._2).map(p => (t._1, p))
          })
          .flatMap(t => getPositionForLink(d, t._1).map(target => (t._2, target)))
          .map(t => RelationshipLink(t._1, t._2))
      case _ => Seq.empty
    }

  private def extractDiscriminatorTypes(d: Dialect, o: UnionNodeMapping, linkName: Seq[(String, String)]) =
    o.annotations.ast().flatMap {
      case a: YMap =>
        a.entries
          .find(_.key.value.toString == "typeDiscriminator")
          .flatMap(t =>
            t.value.value match {
              case m: YMap => Some(m.entries.map(_.value))
              case _       => None
          })
      case _ => None
    } match {
      case Some(a: Seq[YNode]) =>
        linkName
          .flatMap(t => {
            a.find(_.toString == t._2).map(p => (t._1, p))
          })
          .flatMap(t => getPositionForLink(d, t._1).map(target => (t._2, target)))
          .map(t => RelationshipLink(t._1, t._2))
      case _ => Seq.empty
    }

  private def extractExtends(d: Dialect, o: NodeMapping): Seq[RelationshipLink] =
    o.extend.flatMap {
      case e: NodeMapping =>
        o.annotations.ast().flatMap {
          case entry: YMapEntry =>
            entry.value
              .toOption[YMap]
              .map(_.entries)
              .getOrElse(Nil)
              .find(_.key.value.toString == "extends")
              .map(_.value.value)
              .flatMap(source => e.linkTarget.map(target => (source, target)))
              .flatMap(t =>
                getPositionForLink(d, t._2.id)
                  .map(RelationshipLink(t._1, _)))
          case _ => None
        }
    }

  private def extractEncoded(d: Dialect, dm: DocumentsModel): Option[RelationshipLink] =
    (Option(dm.root()).flatMap(_.encoded().annotations().ast()), Option(dm.root()).flatMap(_.encoded().option())) match {
      case (Some(entry), Some(link)) =>
        getPositionForLink(d, link).map(RelationshipLink(entry, _))
      case (_, _) => None
    }

  private def extractDeclarations(d: Dialect, dm: DocumentsModel): Seq[Option[RelationshipLink]] =
    Option(dm.root())
      .map { r =>
        r.declaredNodes()
          .map(pnm =>
            (pnm.mappedNode().annotations().ast(), pnm.mappedNode().option()) match {
              case (Some(ast), Some(link)) =>
                getPositionForLink(d, link).map(RelationshipLink(ast, _))
              case (_, _) => None
          })
      }
      .getOrElse(Nil)

  private def getLink(obj: AmfObject): Option[String] =
    obj.fields
      .fields()
      .find(_.field.value.iri() == PropertyMappingModel.ObjectRange.value.iri())
      .flatMap { entry =>
        entry.value.value match {
          case array: AmfArray =>
            array.values.headOption.flatMap {
              case s: AmfScalar =>
                Some(s.value.toString)
              case _ => None
            }
          case _ => None
        }
      }

  private def getPositionForLink(d: Dialect, link: String): Option[YMapEntry] =
    d.declares
      .find(_.id == link)
      .flatMap(e => {
        e.annotations
          .ast()
          .collectFirst({ case e: YMapEntry => e })
      })

  // todo: tmp hack until node mapping object contains all the entry as ast
  private def seachNodeMapping(root: YMap, mapping: YMap) = {
    root.entries.find(e => e.key.asScalar.map(_.text).getOrElse("") == "")
  }

}

object AMLDialectVisitor extends DialectElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[AMLDialectVisitor] =
    if (applies(bu))
      Some(new AMLDialectVisitor(bu.asInstanceOf[Dialect]))
    else None
}
