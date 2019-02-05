package org.mulesoft.language.outline.structure.structureDefault

import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.KeyProvider

class DefaultKeyProvider extends KeyProvider {
  /**
    * Returns key for the node
    *
    * @param node
    * @return
    */
  def getKey(node: IParseResult): String = {

    if (node.parent.isEmpty) {

      NodeNameProvider.getNodeName(node)
    }
    else {

      NodeNameProvider.getNodeName(node) + " :: " + getKey(node.parent.get)
    }
  }
}
