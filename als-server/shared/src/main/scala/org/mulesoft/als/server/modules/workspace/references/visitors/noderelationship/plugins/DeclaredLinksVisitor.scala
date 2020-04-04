package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.{SourceAST, SourceNode}
import amf.core.parser.{Position => AmfPosition}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.domain.{AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import org.mulesoft.als.actions.common.{ActionTools, RelationshipLink}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.lsp.feature.common.Location

/**
  * @test: org.mulesoft.als.server.modules.definition.files.DefinitionFilesTest - oas-anchor
  */
class DeclaredLinksVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element match {
      case obj: AmfObject =>
        obj.fields
          .entry(LinkableElementModel.Target)
          .flatMap { fe =>
            fe.value.value.annotations
              .find(classOf[SourceNode])
              .map { sn =>
                Seq(
                  RelationshipLink(
                    ActionTools.sourceLocationToLocation(obj.annotations.sourceLocation),
                    ActionTools.sourceLocationToLocation(sn.node.location),
                    getParentLocation(fe)
                  ))
              }
          }
          .getOrElse(Nil)
      case _ => Nil
    }

  private def getParentLocation(fe: FieldEntry): Option[Location] = {
    fe.value.value.annotations
      .find(classOf[SourceAST])
      .map(a =>
        Location(
          a.ast.location.sourceName,
          LspRangeConverter.toLspRange(PositionRange(
            Position(AmfPosition(0, a.ast.location.columnFrom)), // todo: find a nicer way to take parent entry?
            Position(AmfPosition(a.ast.location.lineTo, a.ast.location.columnTo))
          ))
      ))
  }
}

object DeclaredLinksVisitor extends AmfElementVisitorFactory {
  override def apply(): DeclaredLinksVisitor = new DeclaredLinksVisitor()
}
