package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.DeclaredElement
import amf.core.metamodel.domain.{LinkableElementModel, ShapeModel}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.domain.webapi.models.security.SecurityRequirement
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
        extractOrigin(obj)
          .flatMap(origin => extractTarget(obj).map { (origin, _) })
          .map(t => createRelationship(t._1, t._2))
          .getOrElse(Nil)
      case obj: AmfObject if obj.fields.entry(ShapeModel.Inherits).isDefined =>
        extractInherits(obj)
      case sr: SecurityRequirement =>
        extractSecuritySchemes(sr)
      case _ => Nil
    }

  private def extractSecuritySchemes(sr: SecurityRequirement) =
    sr.schemes
      .flatMap(s => Option(s.scheme))
      .flatMap(t => t.annotations.ast().map((t, _)))
      .flatMap(target => sr.annotations.ast().map(source => (source, target)))
      .map(t => RelationshipLink(t._1, t._2._2, getName(t._2._1)))

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
              case array: AmfArray =>
                array.values
                  .filter(e => e.annotations.contains(classOf[DeclaredElement]))
                  .flatMap(v => v.annotations.ast().map((v, _)))
              case o =>
                if (o.annotations.contains(classOf[DeclaredElement]))
                  o.annotations.ast().toSeq.map((o, _))
                else Seq.empty
            }).map(t => RelationshipLink(source, t._2, getName(t._1))))
      }
      .getOrElse(Nil)

  private def extractOrigin(obj: AmfObject): Option[YPart] =
    obj.annotations
      .ast()
      .map(checkYNodePlain)

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
  override def apply(bu: BaseUnit): Option[DeclaredLinksVisitor] =
    if (applies(bu)) Some(new DeclaredLinksVisitor())
    else None
  override def applies(bu: BaseUnit): Boolean =
    !bu.isInstanceOf[Dialect] // targets are strange in Dialects, will be taken in another visitor
}
