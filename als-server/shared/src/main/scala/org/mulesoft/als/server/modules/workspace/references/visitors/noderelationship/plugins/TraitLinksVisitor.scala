package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.{SourceAST, SourceNode}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.metamodel.domain.templates.ParametrizedDeclarationModel
import amf.core.model.domain.templates.{AbstractDeclaration, ParametrizedDeclaration}
import amf.core.model.domain.{AmfArray, AmfElement}
import amf.core.parser.FieldEntry
import amf.core.vocabulary.Namespace
import amf.plugins.domain.webapi.models.Operation
import org.mulesoft.als.actions.common.ActionTools
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.YMapEntry

class TraitLinksVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Option[Result] =
    element match {
      case o: Operation =>
        o.fields
          .fields()
          .find(fe => fe.field.value == Namespace.Document + "extends") // todo: possible to find entry?
          .flatMap(parametrizedDeclarationTargetsWithPosition(_, o))
      case _ => None
    }

  private def parametrizedDeclarationTargetsWithPosition(fe: FieldEntry, o: Operation): Option[(Location, Location)] = {
    fe.value.value match {
      case array: AmfArray =>
        array.values.flatMap {
          case p: ParametrizedDeclaration =>
            p.fields
              .entry(ParametrizedDeclarationModel.Target)
              .flatMap(
                fe =>
                  fe.value.value match {
                    case a: AbstractDeclaration =>
                      a.fields
                        .entry(LinkableElementModel.Target)
                        .flatMap(fieldEntryToLocation(_, p))
                    case _ => None
                }
              )
          case _ => None
        }.headOption
      case _ => None
    }
  }

  private def fieldEntryToLocation(fe: FieldEntry, p: ParametrizedDeclaration): Option[(Location, Location)] = {
    fe.value.value.annotations
      .find(classOf[SourceNode])
      .map { sn =>
        val sourceLocation = p.annotations
          .find(classOf[SourceAST])
          .map { sast =>
            sast.ast match {
              case entry: YMapEntry =>
                entry.value.value.range
                SourceLocation(entry.sourceName,
                               entry.value.value.range.lineFrom,
                               entry.value.value.range.columnFrom,
                               entry.value.value.range.lineTo,
                               entry.value.value.range.columnTo)
              case _ => p.annotations.sourceLocation
            }
          }
          .getOrElse(p.annotations.sourceLocation)
        (ActionTools.sourceLocationToLocation(sourceLocation), ActionTools.sourceLocationToLocation(sn.node.location))
      }
  }
}

object TraitLinksVisitor extends AmfElementVisitorFactory {
  override def apply(): TraitLinksVisitor = new TraitLinksVisitor()
}
