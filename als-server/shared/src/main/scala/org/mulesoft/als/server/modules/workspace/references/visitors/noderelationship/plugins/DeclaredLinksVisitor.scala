package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.metamodel.domain.{LinkableElementModel, ShapeModel}
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.amfintegration.AmfImplicits._
import org.yaml.model._

/**
  * @test: org.mulesoft.als.server.modules.definition.files.DefinitionFilesTest - oas-anchor
  */
class DeclaredLinksVisitor extends NodeRelationshipVisitorType {

  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element match {
      case obj: AmfObject if obj.fields.entry(LinkableElementModel.Target).isDefined =>
        extractTarget(obj)
      case obj: AmfObject if obj.fields.entry(ShapeModel.Inherits).isDefined =>
        extractInherits(obj)
      case _ => Nil
    }

  private def extractTarget(obj: AmfObject) =
    obj.fields
      .entry(LinkableElementModel.Target)
      .flatMap { fe =>
        fe.value.value.annotations
          .ast()
          .flatMap(
            target =>
              obj.annotations
                .ast()
                .map(checkYNodePlain)
                .map(source => (source, target)))
          .map(t => RelationshipLink(t._1, t._2))
      }
      .toSeq

  private def extractInherits(obj: AmfObject) =
    obj.fields
      .entry(ShapeModel.Inherits)
      .flatMap { fe =>
        fe.value.annotations
          .ast()
          .map {
            case e: YMapEntry => e.value
            case o            => o
          }
          .map(source =>
            (fe.value.value match {
              case array: AmfArray => array.values.flatMap(v => v.annotations.ast())
              case o               => o.annotations.ast().toSeq
            }).map(t => RelationshipLink(source, t)))
      }
      .getOrElse(Nil)

  /**
    * checks for {$ref: '#declared'} style references and extracts YMapEntry of such
    *
    * @param sourceEntry
    * @return
    */
  @scala.annotation.tailrec
  private def checkYNodePlain(sourceEntry: YPart): YPart = sourceEntry match {
    case e: YMapEntry  => checkYNodePlain(e.value)
    case p: YNodePlain => checkYNodePlain(p.value)
    case m: YMap       => m.entries.head
    case _             => sourceEntry
  }
}

object DeclaredLinksVisitor extends AmfElementVisitorFactory {
  override def apply(): DeclaredLinksVisitor = new DeclaredLinksVisitor()
}
