package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.domain.{AmfElement, AmfObject, NamedDomainElement}
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitor
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.VirtualYPart
import org.yaml.model.YPart

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

  protected def virtualYPart(maybeSourceName: Option[String],
                             maybeLexical: Option[LexicalInformation],
                             maybeLabel: Option[String]): Option[YPart] =
    (maybeSourceName, maybeLexical, maybeLabel) match {
      case (Some(sourceName), Some(lexical), _) =>
        Some(VirtualYPart(sourceName, lexical, maybeLabel.getOrElse("")))
      case _ => None
    }
}
