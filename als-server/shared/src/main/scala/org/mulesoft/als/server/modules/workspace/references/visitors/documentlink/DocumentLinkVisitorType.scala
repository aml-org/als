package org.mulesoft.als.server.modules.workspace.references.visitors.documentlink

import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitor
import org.mulesoft.lsp.feature.link.DocumentLink

trait DocumentLinkVisitorType extends AmfElementVisitor[(String, Seq[DocumentLink])]
