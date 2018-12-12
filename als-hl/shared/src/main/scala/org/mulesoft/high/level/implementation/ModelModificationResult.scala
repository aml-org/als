package org.mulesoft.high.level.implementation

import org.mulesoft.high.level.interfaces.IModelModificationResult

class ModelModificationResult(_content:String) extends IModelModificationResult {

    override def content: String = _content
}

object ModelModificationResult {
    def apply(_content: String): ModelModificationResult = new ModelModificationResult(_content)
}
