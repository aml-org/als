package org.mulesoft.als.suggestions.implementation

import org.mulesoft.als.suggestions.interfaces.IEditorStateProvider

class DummyEditorStateProvider(text:String,path:String,baseName:String,offset:Int) extends IEditorStateProvider{

    override def getText: String = text

    override def getPath: String = path

    override def getBaseName: String = baseName

    override def getOffset: Int = offset
}
