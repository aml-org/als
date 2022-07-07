package org.mulesoft.amfintegration.visitors.noderelationship.plugins

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.model.domain.security.SecurityRequirement
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{AmfArray, AmfElement, AmfObject}
import amf.core.internal.annotations.DeclaredElement
import amf.core.internal.metamodel.domain.{LinkableElementModel, ShapeModel}
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.AmfElementVisitorFactory
import org.mulesoft.amfintegration.visitors.noderelationship.NodeRelationshipVisitorType
import org.yaml.model._

/** @test:
  *   org.mulesoft.als.server.modules.definition.files.DefinitionFilesTest - oas-anchor
  */
class DeclaredLinksVisitor extends NodeRelationshipVisitorType {

  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element match {
      case obj: AmfObject
          if obj.fields.entry(LinkableElementModel.Target).isDefined && !obj.annotations.isRamlTypeExpression =>
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
      .flatMap(source => {
        val optionTarget = Option(source.scheme)
        optionTarget.flatMap(t => t.annotations.ypart().map(targetAST => (source, t, targetAST)))
      })
      .flatMap(triple =>
        triple._1.annotations
          .ypart()
          .map(sourceAST => RelationshipLink(sourceAST, triple._3, getName(triple._2), getName(triple._1))))

  private def extractInherits(obj: AmfObject): Seq[RelationshipLink] =
    obj.fields
      .entry(ShapeModel.Inherits)
      .flatMap { fe =>
        fe.value.annotations
          .ypart()
          .map {
            case e: YMapEntry => e.value
            case o            => o
          }
          .map(source =>
            (fe.value.value match {
              case array: AmfArray =>
                array.values
                  .filter(e => e.annotations.contains(classOf[DeclaredElement]))
                  .flatMap(v => v.annotations.ypart().map((v, _)))
              case o =>
                if (o.annotations.contains(classOf[DeclaredElement]))
                  o.annotations.ypart().toSeq.map((o, _))
                else Seq.empty
            }).map(t => RelationshipLink(source, t._2, getName(t._1)))
          )
      }
      .getOrElse(Nil)

  private def extractOrigin(obj: AmfObject): Option[YPart] =
    obj.annotations
      .ypart()
      .map(checkYNodePlain)

  /** checks for {$ref: '#declared'} style references and extracts YMapEntry of such
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
