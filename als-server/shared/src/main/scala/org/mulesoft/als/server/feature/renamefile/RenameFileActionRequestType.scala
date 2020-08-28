package org.mulesoft.als.server.feature.renamefile

import org.mulesoft.lsp.feature.RequestType

case object RenameFileActionRequestType extends RequestType[RenameFileActionParams, RenameFileActionResult]
