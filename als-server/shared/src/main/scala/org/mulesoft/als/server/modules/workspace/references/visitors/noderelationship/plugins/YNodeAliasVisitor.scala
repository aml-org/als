package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.plugins

import amf.core.annotations.SourceNode
import amf.core.model.domain.AmfElement
import org.mulesoft.als.actions.common.ActionTools
import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitorFactory
import org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship.NodeRelationshipVisitorType
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YNode, YPart}

/**
  * @test: org.mulesoft.als.server.modules.definition.files.DefinitionFilesTest - yaml-alias
  */
class YNodeAliasVisitor extends NodeRelationshipVisitorType {
  override protected def innerVisit(element: AmfElement): Seq[(Location, Location)] =
    element.annotations
      .find(classOf[SourceNode])
      .flatMap { s =>
        aliasForNode(s.node)
      }
      .toSeq

  private def aliasForNode(part: YPart): Option[(Location, Location)] =
    part match {
      case alias: YNode.Alias =>
        Some(ActionTools.sourceLocationToLocation(alias.location),
             ActionTools.sourceLocationToLocation(alias.target.location))
      case _ => None
    }
}

object YNodeAliasVisitor extends AmfElementVisitorFactory {
  override def apply(): YNodeAliasVisitor = new YNodeAliasVisitor()
}
