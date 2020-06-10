package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.{SourceAST, SourceNode}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.metamodel.domain.templates.ParametrizedDeclarationModel
import amf.core.model.domain.templates.{AbstractDeclaration, ParametrizedDeclaration}
import amf.core.model.domain.{AmfArray, AmfElement}
import amf.core.parser.FieldEntry
import amf.core.vocabulary.Namespace
import amf.plugins.domain.webapi.models.{EndPoint, Operation}
import org.mulesoft.als.actions.common.LinkTypes.LinkTypes
import org.mulesoft.als.actions.common.{LinkTypes, RelationshipLink}
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.yaml.model.YMapEntry

/**
  * @test: org.mulesoft.als.server.modules.definition.files.DefinitionFilesTest - raml-test 1/2
  */
class TraitLinksVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element match {
      case o: Operation =>
        extractFromEntries(o.fields.fields())
      case e: EndPoint =>
        extractFromEntries(e.fields.fields())
      case _ => Nil
    }

  private def extractFromEntries(entries: Iterable[FieldEntry]): Seq[RelationshipLink] = {
    entries
      .find(fe => fe.field.value == Namespace.Document + "extends")
      .map(parametrizedDeclarationTargetsWithPosition)
      .getOrElse(Nil)
  }

  private def parametrizedDeclarationTargetsWithPosition(fe: FieldEntry): Seq[RelationshipLink] = {
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
                        .flatMap(fieldEntryToLocation(_, p, LinkTypes.TRAITRESOURCES))
                    case _ => None
                }
              )
          case _ => None
        }
      case _ => Nil
    }
  }

  private def fieldEntryToLocation(fe: FieldEntry,
                                   p: ParametrizedDeclaration,
                                   linkTypes: LinkTypes): Option[RelationshipLink] = {
    val maybeParent = fe.value.value.annotations
      .find(classOf[SourceAST])
      .map(_.ast)
      .collect { case entry: YMapEntry => entry }
    fe.value.value.annotations
      .find(classOf[SourceNode])
      .flatMap { sn =>
        locationFromObj(p)
          .map { sourceEntry =>
            RelationshipLink(
              sourceEntry,
              maybeParent.getOrElse(sn.node),
              linkTypes
            )
          }
      }
  }
}

object TraitLinksVisitor extends AmfElementVisitorFactory {
  override def apply(): TraitLinksVisitor = new TraitLinksVisitor()
}
