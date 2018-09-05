package org.mulesoft.als.suggestions.implementation

import amf.core.remote.Vendor
import org.mulesoft.als.suggestions.interfaces.{IASTProvider, Syntax}
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}

class EmptyASTProvider(_vendor:Vendor, _syntax:Syntax) extends IASTProvider{

    override def getASTRoot: IHighLevelNode = null

    override def getSelectedNode: Option[IParseResult] = None

    override def language: Vendor = _vendor

    override def syntax: Syntax = _syntax
}
