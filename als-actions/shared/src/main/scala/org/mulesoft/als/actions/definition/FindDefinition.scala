package org.mulesoft.als.actions.definition

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.actions.definition.files.FindDefinitionFile
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.lsp.feature.common.LocationLink

/**
  * Cases in which to return a Location:
  * * root key "uses" indicates there is a map with libraries (whose values are relative paths)
  * * SYAML MutRefs will be returned as location
  * * references will be returned as location (!include or $ref)
  * *  [X] if clicked on a URI it will also return this as location
  */
object FindDefinition extends FindDefinitionFile {
  def getDefinition(bu: BaseUnit, position: Position, platform: Platform): Seq[LocationLink] =
    getDefinitionFile(bu, position, platform)
}
