package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.high.level.interfaces.IFSProvider

trait IExtendedFSProvider extends IFSProvider{

    def contentDirName(content: IEditorStateProvider): String
}
