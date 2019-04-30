
package org.mulesoft.als.server.modules.completion

import org.mulesoft.als.suggestions.interfaces.IEditorStateProvider

class EditorStateProvider(text: String, path: String, baseName: String, offset: Int) extends IEditorStateProvider {

  override def getText: String = text

  override def getPath: String = path

  override def getBaseName: String = baseName

  override def getOffset: Int = offset
}