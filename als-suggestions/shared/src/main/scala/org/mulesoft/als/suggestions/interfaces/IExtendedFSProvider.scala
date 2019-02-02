package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.high.level.interfaces.DirectoryResolver

trait IExtendedFSProvider extends DirectoryResolver {

  def contentDirName(content: IEditorStateProvider): String
}
