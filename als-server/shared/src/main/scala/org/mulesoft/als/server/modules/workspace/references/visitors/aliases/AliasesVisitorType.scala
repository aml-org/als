package org.mulesoft.als.server.modules.workspace.references.visitors.aliases

import org.mulesoft.als.server.modules.workspace.references.visitors.AmfElementVisitor
import org.mulesoft.lexer.SourceLocation

trait AliasesVisitorType extends AmfElementVisitor[(SourceLocation, SourceLocation)]
