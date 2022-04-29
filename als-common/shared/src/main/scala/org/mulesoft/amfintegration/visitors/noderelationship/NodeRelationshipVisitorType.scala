package org.mulesoft.amfintegration.visitors.noderelationship

import amf.core.client.scala.model.domain.{AmfElement, AmfObject, NamedDomainElement}
import amf.core.internal.annotations.{LexicalInformation, SourceAST}
import amf.core.internal.metamodel.domain.LinkableElementModel
import amf.shapes.client.scala.model.domain.NodeShape
import org.mulesoft.als.common.SemanticNamedElement.ElementNameExtractor
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
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
        lazy val name  = ns.name.annotations.ast()
        lazy val nsAst = ns.annotations.ast()
        nameOrEntryKey(name, nsAst)
      case n: NamedDomainElement =>
        lazy val name = n.name
          .annotations()
          .ast()
        lazy val nAst = n.annotations.ast()
        nameOrEntryKey(name, nAst) // security schemes in RAML 0.8 are bringing faulty name AST (unknown location)
      case o: AmfObject =>
        val name = o
          .namedField()
          .flatMap(n =>
            n.value.annotations
              .ast()
          )
        lazy val oAst = o.annotations.ast()
        nameOrEntryKey(name, oAst)
      case _ => None
    }

  private def nameOrEntryKey(name: Option[YPart], ast: Option[YPart]) =
    if (name.exists(_.location == SourceLocation.Unknown)) {
      ast
        .flatMap({
          case entry: YMapEntry if name.exists(_.toString == entry.key.value.toString) => Some(entry.key.value)
          case _                                                                       => None
        })
    } else name

  protected def virtualYPart(
      maybePart: Option[YPart],
      maybeLabel: Option[String],
      maybeLexical: Option[LexicalInformation]
  ): Option[YPart] =
    (maybePart, maybeLabel) match {
      case (Some(originalPart: YScalar), Some(label)) =>
        Some(VirtualYPart(originalPart, label, maybeLexical))
      case (Some(originalPart: YNode), Some(label)) if originalPart.asScalar.isDefined =>
        Some(VirtualYPart(originalPart.asScalar.get, label, maybeLexical))
      case _ =>
        None
    }
}
