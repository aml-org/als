package org.mulesoft.amfintegration.visitors.noderelationship.plugins

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfElement
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.AmfElementVisitorFactory
import org.mulesoft.amfintegration.visitors.noderelationship.NodeRelationshipVisitorType

class ExternalNodeReferenceVisitor() extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element.annotations.externalJsonSchemaShape
      .flatMap { originalEntry =>
        element.annotations
          .ypart()
          .map { (originalEntry, _) }
      }
      .map { t =>
        RelationshipLink(t._1.value, t._2, getName(element))
      }
      .toSeq
}

object ExternalNodeReferenceVisitor extends AmfElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[ExternalNodeReferenceVisitor] =
    if (applies(bu))
      Some(new ExternalNodeReferenceVisitor())
    else None
}
