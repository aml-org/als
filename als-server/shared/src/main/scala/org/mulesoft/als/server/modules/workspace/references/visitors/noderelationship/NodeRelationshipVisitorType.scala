package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship

import amf.core.annotations.SourceAST
import amf.core.model.domain.{AmfElement, AmfObject}
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitor
import org.mulesoft.amfmanager.AmfImplicits._
import org.yaml.model.YPart

trait NodeRelationshipVisitorType extends AmfElementVisitor[RelationshipLink] {

  protected def locationFromObj(obj: AmfElement): Option[YPart] =
    obj.annotations.find(classOf[SourceAST]).map(_.ast)

  protected def getName(a: AmfElement): Option[PositionRange] =
    a match {
      case o: AmfObject =>
        o.namedField()
          .flatMap(
            n =>
              n.annotations
                .range()
                .map(PositionRange(_)))
      case _ => None
    }
}
