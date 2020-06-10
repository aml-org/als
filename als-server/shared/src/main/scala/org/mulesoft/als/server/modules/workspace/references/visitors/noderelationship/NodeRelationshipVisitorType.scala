package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship

import amf.core.annotations.SourceAST
import amf.core.model.domain.AmfElement
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitor
import org.yaml.model.YPart

trait NodeRelationshipVisitorType extends AmfElementVisitor[RelationshipLink] {

  protected def locationFromObj(obj: AmfElement): Option[YPart] =
    obj.annotations.find(classOf[SourceAST]).map(_.ast)
}