package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfElement, AmfObject, NamedDomainElement}
import amf.core.traversal.iterator.DomainElementStrategy
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.server.modules.workspace.references.visitors.WebApiElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.amfintegration.AmfImplicits._
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
            virtualYPart(
              o.annotations.location().orElse(rootEntry.annotations.location()),
              o.annotations.lexicalInformation(),
              o.annotations.ramlExpression().orElse {
                getName(target).collect {
                  case n: YScalar => n.text
                  case n: YNode   => n.toString()
                }
              }
            ).map { (_, target) }
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
