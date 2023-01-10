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
import org.mulesoft.common.client.lexical.SourceLocation
import org.mulesoft.lexer.AstToken
import org.yaml.lexer.YamlToken.Text
import org.yaml.model.{YMapEntry, YNode, YNonContent, YPart, YScalar, YTag, YType}

trait NodeRelationshipVisitorType extends AmfElementVisitor[RelationshipLink] {
  protected def extractTarget(obj: AmfObject): Option[AmfElement] =
    obj.fields
      .entry(LinkableElementModel.Target)
      .map(_.value.value)

  protected def createRelationship(origin: YPart, target: AmfElement): Seq[RelationshipLink] =
    target.annotations
      .yPart()
      .map(targetEntry => (origin, targetEntry))
      .map(t => RelationshipLink(t._1, t._2, getName(target)))
      .toSeq

  protected def getName(a: AmfElement): Option[YPart] =
    a match {
      case a if a.annotations.targetName().isDefined =>
        a.annotations
          .targetName()
          .map(_.name match {
            case n: YNode if n.tagType == YType.Str =>
              n.value.children
                .collectFirst {
                  case yNon: YNonContent if yNon.tokens.exists(_.tokenType == Text) =>
                    yNon.tokens.find(_.tokenType == Text).get
                }
                .map(textToken => VirtualYPart(textToken.location, textToken.text))
                .getOrElse(n.value)
            case p => p
          })
      case ns: NodeShape =>
        lazy val name  = ns.name.annotations.yPart()
        lazy val nsAst = ns.annotations.yPart()
        nameOrEntryKey(name, nsAst)
      case n: NamedDomainElement =>
        lazy val name = n.name
          .annotations()
          .yPart()
        lazy val nAst = n.annotations.yPart()
        nameOrEntryKey(name, nAst) // security schemes in RAML 0.8 are bringing faulty name AST (unknown location)
      case o: AmfObject =>
        val name = o
          .namedField()
          .flatMap(n =>
            n.value.annotations
              .yPart()
          )
        lazy val oAst = o.annotations.yPart()
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
