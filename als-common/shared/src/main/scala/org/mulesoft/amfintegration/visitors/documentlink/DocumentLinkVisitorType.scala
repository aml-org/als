package org.mulesoft.amfintegration.visitors.documentlink

import org.mulesoft.amfintegration.visitors.AmfElementVisitor
import org.mulesoft.lsp.feature.link.DocumentLink

trait DocumentLinkVisitorType extends AmfElementVisitor[(String, Seq[DocumentLink])]
