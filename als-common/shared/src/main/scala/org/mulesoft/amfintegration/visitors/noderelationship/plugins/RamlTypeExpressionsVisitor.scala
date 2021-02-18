package org.mulesoft.amfintegration.visitors.noderelationship.plugins

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfElement, AmfObject, Linkable, NamedDomainElement}
import amf.core.traversal.iterator.DomainElementStrategy
import amf.plugins.domain.shapes.models.ScalarShape
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.WebApiElementVisitorFactory
import org.mulesoft.amfintegration.visitors.noderelationship.NodeRelationshipVisitorType
import org.yaml.model.{YNode, YScalar}

class RamlTypeExpressionsVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element match {
      case o: AmfObject if o.annotations.isRamlTypeExpression =>
        DomainElementStrategy.iterator(List(o)).toSeq.flatMap(e => extractLocation(e, o))
      case _ => Nil
    }

  private def extractLocation(e: AmfElement, rootEntry: AmfObject): Seq[RelationshipLink] =
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

  private def text(rootEntry: AmfObject, o: NamedDomainElement, target: AmfElement): Option[String] =
    o.annotations
      .ramlExpression() // if it has the expression, prioritize
      .orElse { // if there is no info for any expression, let's check the link label
        rootEntry match {
          case l: Linkable =>
            l.linkLabel.option()
          case _ => None
        }
      }
      .orElse {
        o match {
          case s: ScalarShape => s.linkLabel.option()
          case _              => None
        }
      }
      .orElse(o.annotations.sourceNodeText()) // if not, lets check the original node content
      .orElse { // if nothing else found a result, let's check the original name
        getName(target).collect {
          case n: YScalar => n.text
          case n: YNode   => n.toString()
        }
      }
}

object RamlTypeExpressionsVisitor extends WebApiElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[RamlTypeExpressionsVisitor] =
    if (applies(bu))
      Some(new RamlTypeExpressionsVisitor())
    else None
}
