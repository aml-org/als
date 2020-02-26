package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.SourceNode
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.domain.{AmfElement, AmfObject}
import org.mulesoft.als.actions.common.ActionTools
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType

class DeclaredLinksVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Option[Result] =
    element match {
      case obj: AmfObject =>
        obj.fields
          .entry(LinkableElementModel.Target)
          .flatMap { fe =>
            fe.value.value.annotations
              .find(classOf[SourceNode])
              .map { sn =>
                (ActionTools.sourceLocationToLocation(obj.annotations.sourceLocation),
                 ActionTools.sourceLocationToLocation(sn.node.location))
              }
          }
      case _ => None
    }
}

object DeclaredLinksVisitor extends AmfElementVisitorFactory {
  override def apply(): DeclaredLinksVisitor = new DeclaredLinksVisitor()
}
