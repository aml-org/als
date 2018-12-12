package org.mulesoft.als.suggestions.interfaces

import amf.core.remote.Vendor
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}

trait IASTProvider {
    def getASTRoot: IHighLevelNode

    def getSelectedNode: Option[IParseResult]

    def language:Vendor

    def syntax: Syntax
}
