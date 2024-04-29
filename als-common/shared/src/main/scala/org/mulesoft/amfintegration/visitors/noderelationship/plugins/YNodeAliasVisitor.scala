package org.mulesoft.amfintegration.visitors.noderelationship.plugins

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfElement
import amf.core.internal.annotations.SourceNode
import org.mulesoft.amfintegration.relationships.RelationshipLink
import org.mulesoft.amfintegration.visitors.AmfElementVisitorFactory
import org.mulesoft.amfintegration.visitors.noderelationship.NodeRelationshipVisitorType
import org.yaml.model.{YNode, YPart}

/** @test:
  *   org.mulesoft.als.server.modules.definition.files.DefinitionFilesTest - yaml-alias
  */
class YNodeAliasVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element.annotations
      .find(classOf[SourceNode])
      .flatMap { s =>
        aliasForNode(s.node, element)
      }
      .toSeq

  private def aliasForNode(part: YPart, element: AmfElement): Option[RelationshipLink] =
    part match {
      case alias: YNode.Alias =>
        Some(RelationshipLink(alias, alias.target, getName(element)))
      case _ => None
    }
}

object YNodeAliasVisitor extends AmfElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[YNodeAliasVisitor] = Some(new YNodeAliasVisitor())
}
