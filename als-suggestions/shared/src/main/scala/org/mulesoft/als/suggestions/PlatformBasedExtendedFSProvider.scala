package org.mulesoft.als.suggestions

import org.mulesoft.high.level.implementation.PlatformFsProvider
import amf.core.remote.{Context, Platform}
import org.mulesoft.als.suggestions.interfaces.{IEditorStateProvider, IExtendedFSProvider}

class PlatformBasedExtendedFSProvider(platform: Platform) extends PlatformFsProvider(platform)
  with IExtendedFSProvider {

  def contentDirName(content: IEditorStateProvider): String = {

    super.dirName(content.getPath)
  }

}
