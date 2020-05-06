package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.{SourceAST, SourceNode}
import amf.core.parser.{Position => AmfPosition}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.domain.{AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import org.mulesoft.als.actions.common.{ActionTools, RelationshipLink}
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YMap, YMapEntry, YNode, YPart, YSequence}

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
                    locationFromObj(obj),
                    ActionTools.sourceLocationToLocation(sn.node.location),
                    getParentLocation(fe)
                  ))
              }
          }
          .getOrElse(Nil)
      case _ => Nil
    }

  private def locationFromObj(obj: AmfObject): Location =
    obj.annotations.find(classOf[SourceAST]) match {
      case Some(ast) =>
        ActionTools.sourceLocationToLocation(findLastChild(ast.ast).location)
      case None => ActionTools.sourceLocationToLocation(obj.annotations.sourceLocation)
    }

  private def findLastChild(ast: YPart): YPart = ast match {
    case m: YMap =>
      if (m.children.size == 1)
        findLastChild(m.children.head)
      else m
    case e: YMapEntry => findLastChild(e.value)
    case a: YSequence =>
      if (a.children.size == 1)
        findLastChild(a.children.head)
      else a
    case n: YNode => findLastChild(n.value)
    case _        => ast
  }

  private def getParentLocation(fe: FieldEntry): Option[Location] = {
    fe.value.value.annotations
      .find(classOf[SourceAST])
      .map(a =>
        Location(
          a.ast.location.sourceName,
          LspRangeConverter.toLspRange(PositionRange(
            Position(AmfPosition(a.ast.location.lineFrom, a.ast.location.columnFrom)), // todo: find a nicer way to take parent entry?
            Position(AmfPosition(a.ast.location.lineTo, a.ast.location.columnTo))
          ))
      ))
  }
}

object DeclaredLinksVisitor extends AmfElementVisitorFactory {
  override def apply(): DeclaredLinksVisitor = new DeclaredLinksVisitor()
}
