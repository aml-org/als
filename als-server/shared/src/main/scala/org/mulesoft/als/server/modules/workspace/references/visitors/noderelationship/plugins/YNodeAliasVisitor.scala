package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.SourceNode
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfElement
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.yaml.model.{YNode, YPart}

/**
  * @test: org.mulesoft.als.server.modules.definition.files.DefinitionFilesTest - yaml-alias
  */
class YNodeAliasVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[RelationshipLink] =
    element.annotations
      .find(classOf[SourceNode])
      .flatMap { s =>
        aliasForNode(s.node)
      }
      .toSeq

  private def aliasForNode(part: YPart): Option[RelationshipLink] =
    part match {
      case alias: YNode.Alias =>
        Some(RelationshipLink(alias, alias.target))
      case _ => None
    }
}

object YNodeAliasVisitor extends AmfElementVisitorFactory {
  override def apply(bu: BaseUnit): Option[YNodeAliasVisitor] = Some(new YNodeAliasVisitor())
}
