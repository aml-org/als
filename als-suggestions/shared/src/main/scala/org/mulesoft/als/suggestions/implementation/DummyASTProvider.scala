package org.mulesoft.als.suggestions.implementation

import amf.core.remote.Vendor
import org.mulesoft.als.suggestions.interfaces.{IASTProvider, Syntax}
import org.mulesoft.high.level.interfaces.{IHighLevelNode, IParseResult}
import org.mulesoft.high.level.interfaces.IProject

class DummyASTProvider(project: IProject, position:Int) extends IASTProvider{

    override def getASTRoot: IHighLevelNode = project.rootASTUnit.rootNode

    override def getSelectedNode: Option[IParseResult] = getASTRoot.getNodeByPosition(position)

    override def language: Vendor = project.language

    override def syntax: Syntax = {
        val node = getSelectedNode.getOrElse(getASTRoot)
        if (node.astUnit.text.trim.startsWith("{")) {
            Syntax.JSON
        }
        else {
            Syntax.YAML
        }
    }
}
