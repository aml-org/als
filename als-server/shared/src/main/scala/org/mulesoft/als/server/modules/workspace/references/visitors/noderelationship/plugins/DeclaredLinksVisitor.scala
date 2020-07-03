package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.SourceNode
import amf.core.metamodel.domain.LinkableElementModel
import amf.core.model.domain.{AmfElement, AmfObject}
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.yaml.model._

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
              .flatMap { sn =>
                locationFromObj(obj).map { sourceEntry =>
                  //fe.value.value.asInstanceOf[NamedDomainElement].name
                  Seq(
                    RelationshipLink(
                      checkYNodePlain(sourceEntry),
                      locationFromObj(fe.value.value).getOrElse(sn.node),
                      getName(fe.value.value)
                    ))
                }
              }
          }
          .getOrElse(Nil)
      case _ => Nil
    }

  /**
    * checks for {$ref: '#declared'} style references and extracts YMapEntry of such
    * @param sourceEntry
    * @return
    */
  private def checkYNodePlain(sourceEntry: YPart): YPart = sourceEntry match {
    case e: YMapEntry =>
      e.value match {
        case p: YNodePlain =>
          p.value match {
            case m: YMap => m.entries.head
            case x       => x
          }
        case _ => e
      }
    case _ => sourceEntry
  }
}

object DeclaredLinksVisitor extends AmfElementVisitorFactory {
  override def apply(): DeclaredLinksVisitor = new DeclaredLinksVisitor()
}
