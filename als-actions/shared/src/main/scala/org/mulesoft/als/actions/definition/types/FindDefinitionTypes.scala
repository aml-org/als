package org.mulesoft.als.actions.definition.types

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.metamodel.domain.templates.ParametrizedDeclarationModel
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfArray
import amf.core.model.domain.templates.{AbstractDeclaration, ParametrizedDeclaration}
import amf.core.parser
import amf.core.parser.FieldEntry
import amf.core.remote.Platform
import amf.core.vocabulary.Namespace
import amf.plugins.domain.webapi.models.Operation
import org.mulesoft.als.actions.common.ActionTools
import org.mulesoft.als.actions.definition.FinderPlugin
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{ObjectInTree, YPartBranch}
import org.mulesoft.lsp.feature.common.LocationLink

/**
  * Use cases:
  *   when a type is defined, wherever this type is used the "Find Definition" action should redirect to the upper definition
  */
object FindDefinitionTypes extends FinderPlugin {

  def find(bu: BaseUnit,
           position: Position,
           objectInTree: ObjectInTree,
           yPartBranch: YPartBranch,
           platform: Platform): Seq[LocationLink] = {
    val traitLinks: Seq[LocationLink] =
      getTraitLinks(objectInTree, position, yPartBranch, platform)
    val declaredLinks = getDeclaredLinks(objectInTree, yPartBranch, platform)

    declaredLinks ++ traitLinks
  }

  private def getDeclaredLinks(objectInTree: ObjectInTree,
                               yPartBranch: YPartBranch,
                               platform: Platform): Seq[LocationLink] = {
    objectInTree.obj.fields
      .entry(LinkableElementModel.Target)
      .flatMap(fieldEntryToLocation(platform, yPartBranch, _))
      .toSeq
  }

  private def getTraitLinks(objectInTree: ObjectInTree,
                            position: Position,
                            yPartBranch: YPartBranch,
                            platform: Platform): Seq[LocationLink] = {
    objectInTree.stack.headOption match {
      case Some(o: Operation) =>
        o.fields
          .fields()
          .find(fe => fe.field.value == Namespace.Document + "extends") // todo: possible to find entry?
          .map(parametrizedDeclarationTargetsWithPosition(_, position.toAmfPosition, yPartBranch, platform))
          .getOrElse(Nil)
      case _ => Nil
    }
  }

  private def parametrizedDeclarationTargetsWithPosition(fe: FieldEntry,
                                                         position: parser.Position,
                                                         yPartBranch: YPartBranch,
                                                         platform: Platform): Seq[LocationLink] = {
    fe.value.value match {
      case array: AmfArray =>
        array.values.flatMap {
          case p: ParametrizedDeclaration if containsPosition(position, p) =>
            p.fields
              .entry(ParametrizedDeclarationModel.Target)
              .flatMap(
                fe =>
                  fe.value.value match {
                    case a: AbstractDeclaration =>
                      a.fields
                        .entry(LinkableElementModel.Target)
                        .flatMap(fieldEntryToLocation(platform, yPartBranch, _))
                    case _ => None
                }
              )
          case _ => None
        }
      case _ => Nil
    }
  }

  private def containsPosition(position: parser.Position, p: ParametrizedDeclaration): Boolean = {
    p.annotations
      .find(classOf[LexicalInformation])
      .exists(
        li =>
          li.range.start
            .compareTo(position) <= 0 && li.range.end
            .compareTo(position) >= 0)
  }

  private def fieldEntryToLocation(platform: Platform,
                                   yPartBranch: YPartBranch,
                                   fe: FieldEntry): Option[LocationLink] = {
    fe.value.value.annotations
      .find(classOf[SourceAST])
      .map { sast =>
        ActionTools.locationToLsp(yPartBranch.node.location, sast.ast.location, platform)
      }
  }
}
