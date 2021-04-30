package org.mulesoft.amfintegration.visitors.noderelationship

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.domain.{AmfElement, AmfObject, NamedDomainElement}
import amf.plugins.domain.shapes.models.NodeShape
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.VirtualYPart
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.AmfElementVisitor
import org.mulesoft.lexer.SourceLocation
import org.yaml.model.{YMapEntry, YNode, YPart, YScalar}

trait NodeRelationshipVisitorType extends AmfElementVisitor[RelationshipLink] {
  protected def extractTarget(obj: AmfObject): Option[AmfElement] =
    obj.fields
      .entry(LinkableElementModel.Target)
      .map(_.value.value)

  protected def createRelationship(origin: YPart, target: AmfElement): Seq[RelationshipLink] =
    target.annotations
      .ast()
      .map(targetEntry => (origin, targetEntry))
      .map(t => RelationshipLink(t._1, t._2, getName(target)))
      .toSeq

  protected def locationFromObj(obj: AmfElement): Option[YPart] =
    obj.annotations.find(classOf[SourceAST]).map(_.ast)

  protected def getName(a: AmfElement): Option[YPart] =
    a match {
      case ns: NodeShape =>
        val name = ns.name.annotations.ast()
        if (name.exists(_.location == SourceLocation.Unknown)) {
          ns.annotations
            .ast()
            .flatMap({
              case entry: YMapEntry if name.exists(_.toString == entry.key.value.toString) => Some(entry.key.value)
              case _                                                                       => None
            })
        } else {
          name
        }
      case n: NamedDomainElement =>
        n.name
          .annotations()
          .ast()
      case o: AmfObject =>
        o.namedField()
          .flatMap(
            n =>
              n.value.annotations
                .ast())
      case _ => None
    }

  protected def virtualYPart(maybePart: Option[YPart],
                             maybeLabel: Option[String],
                             maybeLexical: Option[LexicalInformation]): Option[YPart] =
    (maybePart, maybeLabel) match {
      case (Some(originalPart: YScalar), Some(label)) =>
        Some(VirtualYPart(originalPart, label, maybeLexical))
      case (Some(originalPart: YNode), Some(label)) if originalPart.asScalar.isDefined =>
        Some(VirtualYPart(originalPart.asScalar.get, label, maybeLexical))
      case _ =>
        None
    }
}
