package org.mulesoft.als.server.modules.workspace.references.visitors.noderelationship

import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitor
import org.mulesoft.lsp.feature.common.Location

trait NodeRelationshipVisitorType extends AmfElementVisitor[(Location, Location)]
