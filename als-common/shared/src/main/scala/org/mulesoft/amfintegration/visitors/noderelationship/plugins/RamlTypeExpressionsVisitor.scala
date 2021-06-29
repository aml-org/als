package org.mulesoft.amfintegration.visitors.noderelationship.plugins

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{AmfElement, AmfObject, NamedDomainElement}
import amf.core.client.scala.traversal.iterator.DomainElementStrategy
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.WebApiElementVisitorFactory
import org.mulesoft.amfintegration.visitors.noderelationship.NodeRelationshipVisitorType

class RamlTypeExpressionsVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element match {
      case o: AmfObject if o.annotations.isRamlTypeExpression =>
        DomainElementStrategy.iterator(List(o)).toSeq.flatMap(e => extractLocation(e))
      case _ => Nil
    }

  private def extractLocation(e: AmfElement): Seq[RelationshipLink] =
    e match {
      case o: NamedDomainElement =>
        extractTarget(o)
          .flatMap { target =>
            (target match {
              case named: NamedDomainElement =>
                virtualYPart(o.annotations.ast(), named.name.option(), o.annotations.lexicalInformation())
              case _ =>
                None
            }).map { (_, target) }
          }
          .map { t =>
            createRelationship(t._1, t._2)
          }
          .getOrElse(Nil)
      case _ => Nil
    }
}

object RamlTypeExpressionsVisitor extends WebApiElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[RamlTypeExpressionsVisitor] =
    if (applies(bu))
      Some(new RamlTypeExpressionsVisitor())
    else None
}
